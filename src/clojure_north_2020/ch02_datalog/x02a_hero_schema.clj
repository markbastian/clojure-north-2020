(ns clojure-north-2020.ch02-datalog.x02a-hero-schema
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def schema
  (->> (io/resource "schemas/hero-schema.edn")
       slurp
       edn/read-string))

(defn hero->dh-format [{:keys [publisher] :as hero}]
  (cond-> hero publisher (update :publisher (fn [p] {:name p}))))

(comment
  (require
    '[clojure-north-2020.ch01-data.x02-sample-data :as hd]
    '[clojure.java.io :as io]
    '[datahike.api :as d])

  ;Create the db
  (let [db-dir (doto
                 (io/file "tmp/hero-data-schema")
                 io/make-parents)]
    (def uri (str "datahike:" (io/as-url db-dir))))

  (when-not (d/database-exists? uri)
    (d/create-database uri))

  (def conn (d/connect uri))

  (d/transact conn schema)
  (count (d/transact conn (mapv hero->dh-format (hd/heroes-data))))

  (d/pull @conn '[*] [:name "Spider-Man"])

  (do
    (d/release conn)
    (d/delete-database uri))
  )