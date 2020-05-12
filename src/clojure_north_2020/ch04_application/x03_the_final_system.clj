(ns clojure-north-2020.ch04-application.x03-the-final-system
  (:require [integrant.core :as ig]
            [clojure-north-2020.ch02-datalog.x06-the-ultimate-db :as x06]
            [clojure-north-2020.ch04-application.x02-the-final-handler :as tfh]
            [clojure-north-2020.ch04-application.parts.datahike :as datahike]
            [clojure-north-2020.ch04-application.parts.jetty :as jetty]))

;; # Building a Library of Restartable Components
;; In a prior section we saw how to create a restartable system that contains
;; all of our stateful system components. These components are started and
;; stopped via the init-key and halt-key! multimethods.
;;
;; We also saw how to create a handler that behaves as a function in which all
;; stateful components (or even stateless components such as db as value) are
;; injected into the handler function.
;;
;; Now it's time to combine these concepts to put everything together.
;;
;; ## Creating a library of Components
;; To make things more modular and reusable, we are going to move our
;; multimethods to their own namespace to create a library of components. Take a
;; look at the following:
;; * clojure-north-2020.ch04-application.parts.jetty - This contains the same
;;   multimethod used in the previous namespace with one important change that
;;   we'll discuss in a moment.
;; * clojure-north-2020.ch04-application.parts.datahike - This contains
;;   Integrant multimethods for creating both a datahike database and
;;   connection.
;;
;; ## The wrap-component Middleware
;; The clojure-north-2020.ch04-application.parts.jetty ns now has this function,
;; which wraps the inbound handler from the configuration map:
;; ```
;; (defn wrap-component [handler component]
;;   (fn [request] (handler (into component request))))
;; ```
;; This `middleware` takes a request and pours the configured component into it
;; prior to passing the request into the handler. Note that it is only the
;; server component from the config that gets poured into the request, not the
;; entire system.
;;
;; ### Configuring the Components
;; Here is our config map. It now has 3 components:
;; * ::datahike/database - Configuration required to create a datahike database.
;; * ::datahike/connection - Configuration required to create a datahike
;;   connection. Note that the connection requires a database, which is referred
;;   to using ig/ref but aliased locally as db-config.
;; * ::jetty/server - Config for the web server, which also declares a
;;   dependency on the datahike connection as dh-conn.
;;
;; Recall that our new functional handler needs a dh-conn in each request to
;; invoke datahike-aware functions. When ig/init is called the config map
;; exchanges the values of the map for started instances of each value and the
;; wrap-component middleware add the configured ::jetty/server keys to its own
;; requests. This completes the threading of the stateful component through the
;; handler. Another great advantage to this approach is that the handler knows
;; NOTHING about the config, integrant, or anything else about its calling
;; context. You can just as easily fabricate a request map and assoc a dh-conn
;; into it and feed that into the handler.
(def config
  {::jetty/server        {:host    "0.0.0.0"
                          :port    3000
                          :join?   false
                          :handler #'tfh/handler
                          :dh-conn (ig/ref ::datahike/connection)}
   ::datahike/database   {:db-file         "tmp/the-final-dhdb"
                          :delete-on-halt? true
                          :initial-tx      x06/schema}
   ::datahike/connection {:db-config (ig/ref ::datahike/database)}})

;; ### System Boilerplate
;; Same as before
(defonce ^:dynamic *system* nil)

(defn system [] *system*)

(defn start []
  (alter-var-root #'*system* (fn [s] (if-not s (ig/init config) s))))

(defn stop []
  (alter-var-root #'*system* (fn [s] (when s (ig/halt! s) nil))))

(defn restart [] (stop) (start))
