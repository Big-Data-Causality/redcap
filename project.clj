(defproject com.bigdatacausality/redcap "0.1.6"
  :description "a redcap client"
  :url "https://github.com/Big-Data-Causality/redcap"
  :license  {:name "MIT License"
             :url  "http://opensource.org/licenses/MIT"}
  :aliases
  {"test"  ["exec" "-ep" "(use 'code.test) (def res (run :all)) (System/exit (+ (:failed res) (:thrown res)))"]}
  
  :dependencies [[org.clojure/clojure  "1.11.1"]
                 [org.clojure/data.csv "1.0.1"]
                 [metosin/malli                    "0.14.0"]
                 [xyz.zcaudate/net.http            "4.0.2"]
                 [xyz.zcaudate/std.lib             "4.0.2"]
                 [xyz.zcaudate/std.text            "4.0.2"]
                 [xyz.zcaudate/std.html            "4.0.2"]
                 [xyz.zcaudate/std.json            "4.0.2"]]
  :profiles
  {:dev 
   {:plugins [[lein-exec "0.3.7"]
              [cider/cider-nrepl "0.45.0"]]
    :dependencies [[xyz.zcaudate/code.test           "4.0.2"]
                   [xyz.zcaudate/code.manage         "4.0.2"]
                   [xyz.zcaudate/code.java           "4.0.2"]
                   [xyz.zcaudate/code.maven          "4.0.2"]
                   [xyz.zcaudate/code.doc            "4.0.2"]
                   [xyz.zcaudate/code.dev            "4.0.2"]]}}
  :deploy-repositories [["clojars"
                         {:url  "https://clojars.org/repo"
                          :sign-releases false}]])
