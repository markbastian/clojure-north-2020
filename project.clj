(defproject clojure-north-2020 "0.1.0-SNAPSHOT"
  :description "A self-guided workshop presented at Clojure/north 2020"
  :url "https://github.com/markbastian/clojure-north-2020"
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
  :repl-options {:init-ns clojure-north-2020.core}
  :aliases {"docs" ["marg"
                    "src/clojure_north_2020/core.clj"
                    "src/clojure_north_2020/ch01_data/x01_data.clj"
                    "src/clojure_north_2020/ch01_data/x01_data_solutions.clj"
                    "src/clojure_north_2020/ch01_data/x02_functions.clj"
                    "src/clojure_north_2020/ch01_data/x02_functions_solutions.clj"
                    "src/clojure_north_2020/ch01_data/x03_hero_data.clj"
                    "src/clojure_north_2020/ch01_data/x04_hero_data_solutions.clj"
                    "src/clojure_north_2020/ch01_data/x04_hero_powers_data.clj"
                    "src/clojure_north_2020/ch01_data/x05_supplemental_hero_data.clj"
                    "src/clojure_north_2020/ch01_data/x06_yoda_quotes.clj"
                    "src/clojure_north_2020/ch02_datalog/datahike_utils.clj"
                    "src/clojure_north_2020/ch02_datalog/x00_basics.clj"
                    "src/clojure_north_2020/ch02_datalog/x01_schemas.clj"
                    "src/clojure_north_2020/ch02_datalog/x02_queries.clj"
                    "src/clojure_north_2020/ch02_datalog/x02_queries_solutions.clj"
                    "src/clojure_north_2020/ch02_datalog/x03_hero_schema.clj"
                    "src/clojure_north_2020/ch02_datalog/x04_hero_powers_schema.clj"
                    "src/clojure_north_2020/ch02_datalog/x04_hero_powers_schema_solutions.clj"
                    "src/clojure_north_2020/ch02_datalog/x05_supplemental_hero_data_schema.clj"
                    "src/clojure_north_2020/ch02_datalog/x06_the_ultimate_db.clj"
                    "src/clojure_north_2020/ch02_datalog/x07_queries.clj"
                    "src/clojure_north_2020/ch02_datalog/x07_queries_solutions.clj"
                    "src/clojure_north_2020/ch03_web/x01_basic.clj"
                    "src/clojure_north_2020/ch03_web/x02_routes.clj"
                    "src/clojure_north_2020/ch03_web/x02_routes_solutions.clj"
                    "src/clojure_north_2020/ch03_web/x03_responses.clj"
                    "src/clojure_north_2020/ch03_web/x04_reitit.clj"
                    "src/clojure_north_2020/ch03_web/x05_state.clj"
                    "src/clojure_north_2020/ch03_web/x05_state_solutions.clj"
                    "src/clojure_north_2020/ch04_application/parts/datahike.clj"
                    "src/clojure_north_2020/ch04_application/parts/jetty.clj"
                    "src/clojure_north_2020/ch04_application/x01_integrant.clj"
                    "src/clojure_north_2020/ch04_application/x02_the_final_handler.clj"
                    "src/clojure_north_2020/ch04_application/x03_the_final_system.clj"
                    "src/clojure_north_2020/ch04_application/x04_parting_thoughts.clj"]})
