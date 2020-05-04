(ns clojure-north-2020.ch04-application.x10-system
  (:require [clojure-north-2020.ch01-data.x01-data :as data]
            [clojure-north-2020.ch01-data.x02-hero-data :as sd]
            [clojure-north-2020.ch02-datalog.x03-datahike-batman :as batman]
            [clojure-north-2020.ch04-application.parts.datahike :as datahike]
            [clojure-north-2020.ch04-application.parts.jetty :as jetty]
            [clojure-north-2020.ch04-application.swagger :as sw]
            [datahike.api :as d]
            [integrant.core :as ig]))

(defn handler [_request]
  {:status 200 :body "OK"})

(def config
  {::jetty/server        {:host    "0.0.0.0"
                          :port    3000
                          :join?   false
                          :handler #'sw/handler
                          :dh-conn (ig/ref ::datahike/connection)}
   ::datahike/database   {:db-file         "tmp/batman"
                          :delete-on-halt? true
                          :initial-tx      batman/schema}
   ::datahike/connection {:db-config (ig/ref ::datahike/database)}})

;; ## System Boilerplate
; This is some standard boilerplate that creates a restartable system with the
; following self-explanatory functions:
;
; 1. `(start)` - Start the system
; 1. `(stop)` - Stop the system
; 1. `(restart)` - Restart the system
; 1. `(system)` - Get a handle to the system
(defonce ^:dynamic *system* nil)

(defn system [] *system*)

(defn start []
  (alter-var-root #'*system* (fn [s] (if-not s (ig/init config) s))))

(defn stop []
  (alter-var-root #'*system* (fn [s] (when s (do (ig/halt! s) nil)))))

(defn restart [] (do (stop) (start)))

(comment
  (let [db (::datahike/connection (system))]
    (d/transact db sd/heroes-data))

  (let [db (::datahike/connection (system))]
    (d/transact db data/data))

  (let [db (::datahike/connection (system))]
    (d/transact db [{:name "Joker"
                     :alignment "Chaotic Evil"}
                    {:name "Penguin"
                     :alignment "Neutral Evil"}]))

  )