(ns user
  (:require
   [clojure.string :as str]
   [clojure.pprint :refer (pprint)]
   [mvt-clj.tools :refer [refresh]]))

(def config
  {:api-url "https://redcap.example.com/api/"
   :token "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456"})

(defn init []
  (println "Running sample init"))

(defn reset []
  (refresh :after 'user/init))

