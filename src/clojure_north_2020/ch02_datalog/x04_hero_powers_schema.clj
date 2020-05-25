(ns clojure-north-2020.ch02-datalog.x04-hero-powers-schema
  (:require [clojure-north-2020.ch02-datalog.datahike-utils :as du]))

;; ## Loading the Hero Powers Schema
;; No modification of data is required.
(def schema (du/read-edn "schemas/datahike/hero-powers-schema.edn"))

(comment
  (require
    '[clojure-north-2020.ch01-data.x04-hero-powers-data :as hpd]
    '[datahike.api :as d])

  (def conn (du/conn-from-dirname "tmp/hero-powers-schema"))

  (d/transact conn schema)
  (count (d/transact conn (vec (hpd/powers-data))))

  (d/pull @conn '[*] [:name "Spider-Man"])

  ;; ## Exercise: Who has the power :levitation?
  (d/q
    '[...]
    @conn :levitation)

  ;; ## Exercise: Who has the same powers as the named super? Return a map of
  ;; power to sequence of names of hero with shared power.
  (let [hero-name "Yoda"]
    (->> (d/q
           '[...]
           @conn hero-name)
         (reduce (fn [m [n p]] (update m p conj n)) {})))

  ;; ## Exercise: List heroes by number of powers. Who has the most?
  (sort-by
    second
    (d/q
      '[...]
      @conn))

  (du/cleanup conn)
  )