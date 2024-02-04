(defproject xyz.zcaudate/redcap "0.1.0-SNAPSHOT"
  :description "a redcap client"
  :url "https://github.com/zcaudate-xyz/redcap"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
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
