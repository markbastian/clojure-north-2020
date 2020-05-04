(ns clojure-north-2020.ch04-application.parts.jetty
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [taoensso.timbre :as timbre])
  (:import (org.eclipse.jetty.server Server)))

(defn wrap-component [handler component]
  (fn [request] (handler (into component request))))

(defmethod ig/init-key ::server [_ {:keys [handler] :as m}]
  (timbre/debug "Launching Jetty web server.")
  (jetty/run-jetty (wrap-component handler m) m))

(defmethod ig/halt-key! ::server [_ ^Server server]
  (timbre/debug "Stopping Jetty web server.")
  (.stop server))
