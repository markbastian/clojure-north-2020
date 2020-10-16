(ns clojure-north-2020.ch02-datalog.x03-hero-schema
  (:require [clojure-north-2020.ch02-datalog.datahike-utils :as du]))

;; ## Loading the Hero Schema and Normalizing the Data
;; Here we are loading in our pre-created basic hero schema.
(def schema (du/read-edn "schemas/datahike/hero-schema.edn"))

;; It is common to have a function that transforms the data into a schema
;; compliant format. This can also be achieved with database or transaction
;; functions, which are beyond the scope of this workshop.
(defn hero->dh-format [{:keys [publisher] :as hero}]
  (cond-> hero publisher (update :publisher (fn [p] {:name p}))))

(comment
  (require
    '[clojure-north-2020.ch01-data.x03-hero-data :as hd]
    '[datahike.api :as d])

  (def conn (du/conn-from-dirname "tmp/hero-data-schema"))

  (d/transact conn schema)
  (count (d/transact conn (mapv hero->dh-format (hd/heroes-data))))

  (d/pull @conn '[*] [:name "Spider-Man"])

  (du/cleanup conn)
  )