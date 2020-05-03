(ns clojure-north-2020.ch02-datalog.x03a-hero-powers-schema
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def schema
  (->> (io/resource "schemas/hero-powers-schema.edn")
       slurp
       edn/read-string))

(comment
  (require
    '[clojure-north-2020.ch01-data.x03-hero-powers-data :as hpd]
    '[datahike.api :as d])

  ;Create the db
  (let [db-dir (doto
                 (io/file "tmp/hero-powers-schema")
                 io/make-parents)]
    (def uri (str "datahike:" (io/as-url db-dir))))

  (when-not (d/database-exists? uri)
    (d/create-database uri))

  (def conn (d/connect uri))

  (d/transact conn schema)
  (d/transact conn (vec (hpd/powers-data)))

  (d/q
    '[:find [?name ...]
      :in $ ?power
      :where
      [?e :name ?name]
      [?e :powers ?power]]
    @conn :levitation)

  (->> (d/q
         '[:find ?that-name ?power
           :in $ ?name
           :where
           [?e :name ?name]
           [?e :powers ?power]
           [?f :powers ?power]
           [?f :name ?that-name]
           [(not= ?e ?f)]]
         @conn "Yoda")
       (group-by second)
       (map (fn [[k v]] [k (sort (map first v))]))
       (into {}))

  (sort-by
    second
    (d/q
      '[:find ?name (count ?power)
        :in $
        :where
        [?e :name ?name]
        [?e :powers ?power]]
      @conn))

  (do
    (d/release conn)
    (d/delete-database uri))
  )