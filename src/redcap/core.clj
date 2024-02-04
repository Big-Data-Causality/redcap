(ns redcap.core
  (:require [std.lib :as h]
            [std.string :as str]
            [std.json :as json]
            [std.html :as html]
            [redcap.meta :as meta]
            [redcap.unit :as unit]
            [net.http :as http]
            [malli.core :as m]))

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
           spec] :as unit}
   {:keys [url token return]}
   params]
  (let [input (merge defaults {:token token} params)
        _     (when (not (m/validate spec input))
                (throw (ex-info "Not valid"
                                {:explain (m/explain spec input)})))
        body  (http/encode-form-params input)
        raw   (http/post url {:headers {"Content-Type" "application/x-www-form-urlencoded"
                                        "Accept" "application/json"}
                              :body body})]
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
    [~'{:keys [url token return] :as api}
     {:keys ~(mapv (comp symbol name)
                   (keys (get-in unit [:input :params])) )
      :as ~'params}]
    (call-api (get +units+ ~label)
              ~'api
              ~'params)))

(def +functions+
  (eval (mapv create-api-form (sort +units+))))
