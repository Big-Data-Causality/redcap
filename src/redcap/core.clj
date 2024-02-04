(ns redcap.core
  (:require [std.lib :as h]
            [std.string :as str]
            [redcap.meta :as meta]
            [redcap.unit :as unit]
            [net.http :as http]))

(def +units+
  (mapv unit/generate-unit meta/+api-linear+))



(defn call-api
  [unit {:keys [url token data]}]
  ())
