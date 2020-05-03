(ns clojure-north-2020.ch02-datalog.datahike-utils
  (:require [datahike.api :as d]
            [clojure.java.io :as io]))

(defn conn-from-dirname [dirname]
  (let [db-dir (doto
                 (io/file dirname)
                 io/make-parents)
        uri (str "datahike:" (io/as-url db-dir))
        _ (when-not (d/database-exists? uri)
            (d/create-database uri))
        conn (d/connect uri)]
    (alter-meta! conn assoc :uri uri)
    conn))

(defn cleanup [conn]
  (let [{:keys [uri]} (meta conn)]
    (d/release conn)
    (when uri (d/delete-database uri))))

(comment
  (conn-from-dirname "tmp/abc")
  )