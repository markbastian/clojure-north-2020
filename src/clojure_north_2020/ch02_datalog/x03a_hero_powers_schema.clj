(ns clojure-north-2020.ch02-datalog.x03a-hero-powers-schema
  (:require [clojure-north-2020.ch02-datalog.datahike-utils :as du]))

(def schema (du/read-edn "schemas/datahike/hero-powers-schema.edn"))

(comment
  (require
    '[clojure-north-2020.ch01-data.x04-hero-powers-data :as hpd]
    '[datahike.api :as d])

  (def conn (du/conn-from-dirname "tmp/hero-powers-schema"))

  (d/transact conn schema)
  (count (d/transact conn (vec (hpd/powers-data))))

  (d/pull @conn '[*] [:name "Spider-Man"])

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
       (reduce (fn [m [n p]] (update m p conj n)) {}))

  (sort-by
    second
    (d/q
      '[:find ?name (count ?power)
        :in $
        :where
        [?e :name ?name]
        [?e :powers ?power]]
      @conn))

  (du/cleanup conn)
  )