(ns clojure-north-2020.ch04-application.x10-system
  (:require [integrant.core :as ig]
            [datahike.api :as d]
            [clojure.java.io :as io]
            [clojure-north-2020.ch02-datalog.x03-datahike-batman :as batman]
            [clojure-north-2020.ch01-data.x01-data :as data]
            [clojure-north-2020.ch01-data.x02-sample-data :as sd]
            [clojure-north-2020.ch04-application.swagger :as sw]
            [clojure-north-2020.ch04-application.parts.datahike :as datahike]
            [clojure-north-2020.ch04-application.parts.jetty :as jetty]))


(defn handler [_request]
  {:status 200 :body "OK"})

(def config
  {::jetty/server  {:host    "0.0.0.0"
                    :port    3000
                    :join?   false
                    :handler #'sw/handler
                    :dh-conn (ig/ref ::datahike/conn)}
   ::datahike/conn {:uri     (let [db-dir (doto
                                            (io/file "tmp/batman")
                                            io/make-parents)]
                               (str "datahike:" (io/as-url db-dir)))
                    :delete? true}})

(defonce ^:dynamic *system* nil)

(defn system [] *system*)

(defn start []
  (alter-var-root #'*system* (fn [s] (if-not s (ig/init config) s))))

(defn stop []
  (alter-var-root #'*system* (fn [s] (when s (do (ig/halt! s) nil)))))

(defn restart [] (do (stop) (start)))

(comment
  (let [db (::datahike/conn (system))]
    (d/transact db batman/schema)
    (d/transact db data/data))

  (let [db (::datahike/conn (system))]
    (d/transact db sd/heroes-data))

  )