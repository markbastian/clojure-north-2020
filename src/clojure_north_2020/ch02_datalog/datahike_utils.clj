;; ## A few utility functions for working with Datahike
;; Nothing special to see here.
(ns clojure-north-2020.ch02-datalog.datahike-utils
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [datahike.api :as d]))

;; Create a datahike connection backed by the given directory name.
(defn conn-from-dirname [dirname]
  (let [db-dir (doto (io/file dirname) io/make-parents)
        uri (str "datahike:" (io/as-url db-dir))
        _ (when-not (d/database-exists? uri)
            (d/create-database uri))
        conn (d/connect uri)]
    (alter-meta! conn assoc :uri uri)
    conn))

;; Release a datahike connection and delete the database.
(defn cleanup [conn]
  (let [{:keys [uri]} (meta conn)]
    (d/release conn)
    (when uri (d/delete-database uri))))

;; read an edn resource to data.
(defn read-edn [resource]
  (->> (io/resource resource) slurp edn/read-string))

(comment
  (conn-from-dirname "tmp/abc")
  )