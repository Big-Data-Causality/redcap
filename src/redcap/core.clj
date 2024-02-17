(ns redcap.core
  (:require [std.lib :as h]
            [std.string :as str]
            [std.json :as json]
            [std.html :as html]
            [redcap.meta :as meta]
            [redcap.unit :as unit]
            [net.http :as http]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [malli.core :as m]
            [malli.error :as me]))

(defonce ^:dynamic *site-opts* nil)

(defonce ^:dynamic *unit* nil)

(defonce ^:dynamic *input* nil)

(defonce ^:dynamic *interim* nil)

(defonce ^:dynamic *output* nil)

(defn set-site-opts
  "sets the default site opts"
  {:added "0.1"}
  [api]
  (alter-var-root #'*site-opts* (fn [_] api)))

(defmacro with-site-opts
  "binds the actual site opts"
  {:added "0.1"}
  [[api] & body]
  `(binding [*site-opts* ~api]
     ~@body))

(def +units+
  (h/map-juxt
   [(comp keyword :label :input) identity]
   (mapv unit/generate-unit meta/+api-linear+)))

(defn parse-body
  "parses the raw return"
  {:added "0.1"}
  [{:keys [body headers]}]
  (let [{:keys [content-type]} headers]
    (cond (str/starts-with? content-type "application/json")
          (json/read body)

          (str/starts-with? content-type "text/xml")
          (html/tree body)
          
          :else body)))


(defn- url-encode [s]
  (.replace (java.net.URLEncoder/encode s "UTF-8") "+" "%20"))

(defn- encode-form-params
  [params]
  (->> params
       (keep (fn [[k v]]
               (cond (nil? v)
                     nil

                     (vector? v)
                     (->> (map-indexed
                           (fn [i x]
                             (str (url-encode (h/strn k)) "[" (+ i 1) "]" "=" (url-encode (h/strn x))))
                           v)
                          (interpose "&")
                          (apply str))
                     
                     :else
                     (str (url-encode (h/strn k)) "=" (url-encode (h/strn v))))))
       (interpose "&")
       (apply str)))


(defn call-api
  "calls the redcap api"
  {:added "0.1"}
  [{:keys [defaults
           transforms
           spec] :as unit}
   params
   site-opts]
  (let [{:keys [url token return ignore]
         :as site-opts} (merge site-opts
         *site-opts*)
        _     (when (not token)
                (throw (ex-info "Missing values for token" (or site-opts {}))))
        _     (when (not url)
                (throw (ex-info "Missing values for url" (or site-opts {}))))
        input (merge defaults {:token token} params)
        
        _     (alter-var-root #'*input* (fn [_] input))
        _     (alter-var-root #'*unit*  (fn [_] unit))        
        _     (when (and (not ignore)
                         (not (m/validate spec input)))
                (throw (ex-info "Not valid"
                                {:reason (me/humanize (m/explain spec input))})))
        interim  (cond ignore
                       input
                       
                       :else
                       (reduce (fn [interim [k f]]
                                 (update interim k f))
                               input
                               (seq transforms)))
        body  (encode-form-params  interim)
        _     (alter-var-root #'*interim* (fn [_] {:data interim
                                                   :body body}))
        raw   (http/post url {:headers {"Content-Type" "application/x-www-form-urlencoded"
                                        "Accept" "application/json"}
                              :body body})
        _     (alter-var-root #'*output* (fn [_] raw))
        output-fn (or (get-in unit [:output :transform])
                      identity)]
    (cond (= :raw return)
          raw
          
          (= 200 (:status raw))
          (output-fn (parse-body raw))
          
          :else
          (throw (ex-info "API Call Invalid"
                          raw)))))

(defn create-api-form
  "creates an api form"
  {:added "0.1"}
  [[label unit]]
  `(defn ~(symbol (name label))
     [~'& [{:keys ~(mapv (comp symbol name)
                         (keys (get-in unit [:input :params])) )
            :as ~'params}
           ~'{:keys [url token return] :as site-opts}]]
     (call-api (get +units+ ~label)
               ~'params
               ~'site-opts)))

(def +functions+
  (eval (mapv create-api-form (sort +units+))))

(def +metadata-fields+
  ["field_name"
   "form_name"
   "section_header"
   "field_type"
   "field_label"
   "select_choices_or_calculations"
   "field_note"
   "text_validation_type_or_show_slider_number"
   "text_validation_min"
   "text_validation_max"
   "identifier"
   "branching_logic"
   "required_field"
   "custom_alignment"
   "question_number"
   "matrix_group_name"
   "matrix_ranking"
   "field_annotation"])

(defn metadata->csv
  [coll]
  (let [string-writer (java.io.StringWriter.)
        csv-writer    (io/writer string-writer)
        rows          (map (fn [row]
                             (mapv (fn [k] (get row k))
                                   +metadata-fields+))
                           coll)]
    (csv/write-csv csv-writer (apply vector +metadata-fields+ rows))
    (.flush csv-writer)
    (.close csv-writer)
    (.toString string-writer)))

(defn csv->metadata
  [csv]
  (let [[headers & rows] (csv/read-csv csv)
        ks   +metadata-fields+]
    (mapv (fn [row]
            (apply hash-map (interleave ks row)))
          rows)))

(defn export-pipeline
  [& [{:as params, :keys [fields forms]} {:keys [url token return], :as site-opts}]]
  (csv->metadata (export-metadata
                  (merge params {:format "csv"})
                  site-opts)))

(defn import-pipeline
  [coll & [{:keys [url token return], :as site-opts}]]
  (import-metadata
   {:format "csv"
    :data (metadata->csv coll)}
   (merge site-opts
          {:ignore true})))

(defn get-forms
  [pipeline]
  (vec
   (dedupe
    (map #(get % "form_name")
         pipeline))))

(defn get-fields
  [pipeline]
  (reduce (fn [[[form fields] :as arr]
               {:strs [field_name
                       form_name]}]
            (cond (= form form_name)
                  (cons [form_name (cons field_name fields)]
                        (rest arr))
                  
                  :else
                  (cons [form_name (list field_name)]
                        arr)))
          ()
          (mapv #(select-keys % ["field_name"
                                 "form_name"])
                (reverse pipeline))))

(defn get-options
  [pipeline]
  (keep (fn [{:strs [field_type
                     field_name
                     form_name
                     select_choices_or_calculations]}]
          (when (#{"dropdown"
                   "radio"
                   "checkbox"} field_type)
            [[form_name field_name] select_choices_or_calculations]))
        pipeline))
