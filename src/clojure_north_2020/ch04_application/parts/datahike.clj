;; Don't look at this until we get to "x03-the-final-system."
;; All will be revealed.
(ns clojure-north-2020.ch04-application.parts.datahike
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [datahike.api :as d]
            [integrant.core :as ig]
            [taoensso.timbre :as timbre]))

(defn file->datahike-db-uri [file]
  (let [f (io/file file)
        _ (io/make-parents f)]
    (str "datahike:" (io/as-url f))))

(defmethod ig/init-key ::database
  [_ {:keys [db-uri db-file initial-tx schema-on-read temporal-index]
      :as   config}]
  (if-some [uri (or db-uri (file->datahike-db-uri db-file))]
    (let [args (cond-> [uri]
                       initial-tx (conj :initial-tx initial-tx)
                       schema-on-read (conj :schema-on-read schema-on-read)
                       temporal-index (conj :temporal-index temporal-index))]
      (timbre/debug "Ensuring Datahike DB database.")
      (when-not (d/database-exists? uri)
        (timbre/debugf "Datahike DB database does not exist... Creating %s." uri)
        (apply d/create-database args))
      (timbre/debugf "Datahike DB uri: %s" uri)
      (assoc config :db-uri uri))
    (timbre/error "No uri provided for database")))

(defmethod ig/halt-key! ::database [_ {:keys [db-uri delete-on-halt?]}]
  (when delete-on-halt?
    (timbre/debug "Deleting Datahike DB database.")
    (d/delete-database db-uri)))

(defmethod ig/init-key ::connection [_ {:keys [db-uri db-config]}]
  (if-some [uri (or db-uri (:db-uri db-config))]
    (do
      (timbre/debug "Creating Datahike DB connection.")
      (d/connect uri))
    (timbre/error "No db-uri provided for Datahike connection")))

(defmethod ig/halt-key! ::connection [_ connection]
  (if connection
    (do
      (timbre/debug "Releasing Datahike DB connection.")
      (d/release connection))
    (timbre/warn "No Datahike connection to release!")))