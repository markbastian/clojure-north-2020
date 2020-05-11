(ns clojure-north-2020.ch04-application.x02-adding-datahike
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [taoensso.timbre :as timbre]
            [clojure-north-2020.ch03-web.x05-state :as x05-state])
  (:import (org.eclipse.jetty.server Server)))

;; # Building a Library of Restartable Components
;;

;; ### Web Handlers
;; These are just some potential web handlers
(defn handler [_request]
  {:status 200 :body "OK"})

;(def handler x05-state/handler)

;; ### Configuration
;; Here is our config map. It has one key, ::server, which has configuration
;; options for a ring-jetty adapter.
(def config
  {::server {:host    "0.0.0.0"
             :port    3000
             :join?   false
             :handler #'handler}})

;; ### Integrant Implementation
;; To use Integrant, implement the ig/init-key and ig/halt-key (optional)
;; multimethods so that when the system is initialized logic is registered for
;; each key.
(defmethod ig/init-key ::server [_ {:keys [handler] :as m}]
  (timbre/debug "Launching Jetty web server.")
  (jetty/run-jetty handler m))

(defmethod ig/halt-key! ::server [_ ^Server server]
  (timbre/debug "Stopping Jetty web server.")
  (.stop server))

;; ### System Boilerplate
;; We'll now wrap the ig/init and ig/halt! methods in some standard boilerplate
;; to create a restartable system with the following self-explanatory functions:
;;
;; 1. `(start)` - Start the system
;; 1. `(stop)` - Stop the system
;; 1. `(restart)` - Restart the system
;; 1. `(system)` - Get a handle to the system
;;
;; Try them out. What we've done so far may not seem awesome, but we've done a
;; few useful things:
;;
;; * Created a method to centrally contain and manage stateful items
;; * Created a facility to centrally and cleanly manage system states
(defonce ^:dynamic *system* nil)

(defn system [] *system*)

(defn start []
  (alter-var-root #'*system* (fn [s] (if-not s (ig/init config) s))))

(defn stop []
  (alter-var-root #'*system* (fn [s] (when s (ig/halt! s) nil))))

(defn restart [] (stop) (start))
