(defproject xyz.zcaudate/redcap "0.1.0-SNAPSHOT"
  :description "a redcap client"
  :url "https://github.com/zcaudate-xyz/redcap"
  :license  {:name "MIT License"
             :url  "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [metosin/malli                    "0.14.0"]
                 [xyz.zcaudate/net.http            "4.0.1"]
                 [xyz.zcaudate/std.lib             "4.0.1"]
                 [xyz.zcaudate/std.text            "4.0.1"]
                 [xyz.zcaudate/std.html            "4.0.1"]
                 [xyz.zcaudate/std.json            "4.0.1"]]
  :profiles
  {:dev 
   {:dependencies [[xyz.zcaudate/code.test           "4.0.1"]
                   [xyz.zcaudate/code.manage         "4.0.1"]
                   [xyz.zcaudate/code.java           "4.0.1"]
                   [xyz.zcaudate/code.maven          "4.0.1"]
                   [xyz.zcaudate/code.doc            "4.0.1"]
                   [xyz.zcaudate/code.dev            "4.0.1"]]}})
