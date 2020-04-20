(defproject clojure-north-2020 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring/ring-jetty-adapter "1.8.0"]
                 [metosin/ring-http-response "0.9.1"]
                 [hiccup "1.0.5"]
                 [metosin/reitit "0.4.2"]
                 [ring "1.8.0"]
                 [integrant "0.8.0"]
                 [funcool/cuerdas "2.1.0"]
                 [clojure-csv/clojure-csv "2.0.2"]
                 [datascript "0.18.11"]
                 [datascript-transit "0.3.0"]
                 [io.replikativ/datahike "0.2.1"]
                 [com.taoensso/timbre "4.10.0"]]
  :plugins [[marginalia "0.9.1"]]
  :repl-options {:init-ns clojure-north-2020.core})
