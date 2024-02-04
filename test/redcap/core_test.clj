(ns redcap.core-test
  (:use code.test)
  (:require [redcap.core :as rc]))

^{:refer redcap.core/parse-body :added "0.1"}
(fact "parses the raw return"
  ^:hidden

  (rc/parse-body
   {:body "14.0.10",
    :headers {:content-type "text/csv; charset=utf-8"}
    :status 200})
  => "14.0.10"
  
  (rc/parse-body
   {:body "'14.0.10'",
    :headers {:content-type "application/json; charset=utf-8"}
    :status 200})
  => "14.0.10")

^{:refer redcap.core/call-api :added "0.1"}
(fact "calls the redcap api")

^{:refer redcap.core/create-api-form :added "0.1"}
(fact "creates an api form"
  ^:hidden
  
  (rc/create-api-form (last (sort rc/+units+)))
  => '(clojure.core/defn switch-dag [{:keys [url token return], :as api} {:as params, :keys [dag]}]
        (redcap.core/call-api (clojure.core/get redcap.core/+units+ :switch-dag) api params)))

(comment

  (rc/export-version
   {:url +demo-url+
    :token +demo-token+}
   {})
  
  (mapv create-api-form (sort +units+))
  
  (require 'code.manage)
  (url-encode "hello")
  (encode-form-params {:hello "world"})
  
  (def +demo-url+
    "https://redcapdemo.vanderbilt.edu/api/")

  (def +demo-token+
    "8A3B0ED30F7595CC2615A53E7597F0C7")

  (export-record)
  
  (call-api (last +units+)
            (create-api-form (first +units+))
            (clojure.core/defn export-survey-return-code [{:keys [url token data], :as input}] (redcap.core/call-api (clojure.core/get redcap.core/+units+ :export-survey-return-code) input))

            (export-survey-return-code
             {:url +demo-url+
              :token +demo-token+})

            (export-survey-return-code
             {:url +demo-url+
              :token +demo-token+}))

  (export-version
   {:url +demo-url+
    :token +demo-token+})
  
  (export-record
   {:url +demo-url+
    :token +demo-token+}
   {:format "xml"})
  
  
  
  (http/post "https://redcapdemo.vanderbilt.edu/api/",
             {:headers {"Content-Type" "application/x-www-form-urlencoded",
                        "Accept" "application/json"},
              :body "token=8A3B0ED30F7595CC2615A53E7597F0C7&content=version"}))

(comment
  {:url "https://redcapdemo.vanderbilt.edu/api/", :headers {"Content-Type" "application/json", "Accept" "application/json"}, :body "token=8A3B0ED30F7595CC2615A53E7597F0C7&&content=version"})
