(ns clojure-north-2020.ch04-application.parts.datahike
  (:require [datahike.api :as d]
            [taoensso.timbre :as timbre]
            [integrant.core :as ig]))

(defmethod ig/init-key ::conn [_ {:keys [uri] :as config}]
  (timbre/debug "Initializing Datahike.")
  (when-not (d/database-exists? uri)
    (d/create-database uri))
  (let [conn (d/connect uri)]
    (alter-meta! conn into config)
    conn))

(defmethod ig/halt-key! ::conn [_ conn]
  (timbre/debug "Shutting down Datahike.")
  (let [{:keys [uri delete?]} (meta conn)]
    (d/release conn)
    (when delete?
      (d/delete-database uri))))