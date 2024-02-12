(ns redcap.core
  (:require [std.lib :as h]
            [std.string :as str]
            [std.json :as json]
            [std.html :as html]
            [redcap.meta :as meta]
            [redcap.unit :as unit]
            [net.http :as http]
            [malli.core :as m]
            [malli.error :as me]))

(def ^:dynamic *site-opts* nil)

(def ^:dynamic *unit* nil)

(def ^:dynamic *input* nil)

(def ^:dynamic *interim* nil)

(def ^:dynamic *output* nil)

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
                (throw (ex-info "Missing values for token" site-opts)))
        _     (when (not url)
                (throw (ex-info "Missing values for url" site-opts)))
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
        body  (http/encode-form-params  interim)
        _     (alter-var-root #'*interim* (fn [_] {:data interim
                                                   :body body}))
        raw   (http/post url {:headers {"Content-Type" "application/x-www-form-urlencoded"
                                        "Accept" "application/json"}
                              :body body})
        _     (alter-var-root #'*output* (fn [_] raw))]
    (cond (= :raw return)
          raw
          
          (= 200 (:status raw))
          (parse-body raw)
          
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
