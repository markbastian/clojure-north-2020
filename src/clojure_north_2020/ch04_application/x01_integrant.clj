(ns clojure-north-2020.ch04-application.x01-integrant
  (:require [clojure-north-2020.ch03-web.x05-state :as x05-state]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [taoensso.timbre :as timbre])
  (:import (org.eclipse.jetty.server Server)))

;; # Building Reloadable Systems with Integrant
;; Until now we've just used global, stateful components in our systems, namely
;; a jetty web server and a datahike db as needed. This leaves lots of
;; fragmented, global state sprinked about out program. There are also decoupled
;; methods scattered about for initializing and cleaning up each component in
;; the system.
;;
;; It's time to fix this by "pushing our state to the edges of our system." This
;; means we want all stateful components in one controlled location that we can
;; feed into one side of the system (the ingress edge) and treat as values at
;; the other edge of the system (the egress side) such that the system appears
;; functional to the user. Outside of the setup of the system (initialization)
;; and the leaf functions/methods of the system where the actual components are
;; used the entire system behaves as a collection of functions.
;;
;; One great aspect of our design so far is that we really are only a couple of
;; steps away from achieving this design already. With the exception of our two
;; stateful components (web server and connection) the entire api is nothing but
;; functions and data. We don't need to adapt any of these building blocks to
;; our new design. We'll just use our bottom-up set of functions as-is.
;;
;; ## Step 1: Make Centralized, Reloadable Components
;; Integrant (aliased as ig) is a library for describing stateful resource
;; configurations as maps. Upon initialization, each val in the config map is
;; exchanged for a stateful thing using the config data in each key's
;; value. The following example will illustrate the concept.
;;
;; Note that other good libraries exist for handling state, such as Component
;; and Mount.

;; ### Web Handlers
;; In this ns we've just got our single OK handler.
(defn handler [_request]
  {:status 200 :body "OK"})

;; ### Exercise - Change the handler
;; Once you've started the system (below), swap out the handler for the one
;; we've already developed. Note that we're decoupling yet another concern -
;; system state management. You can just pull in existing functions and use
;; them (However, this handler does have issues that we'll fix later).
;(def handler x05-state/handler)

;; ### Configuration
;; Here is our config map. It has one key, ::server, which has configuration
;; options for a ring-jetty adapter. Recall that when ig/init is called the vals
;; of this map will be exchanged for a stateful component. In this case, we will
;; go from the map `{::server config-map}` to
;; `{::server web-server-using-the-config-map}`.
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
;; ### Exercise - Try them out.
;; What we've done so far may not seem awesome, but we've done a few useful
;; things:
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