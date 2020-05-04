(ns clojure-north-2020.ch02-datalog.x03a-hero-schema
  (:require [clojure-north-2020.ch02-datalog.datahike-utils :as du]))

(def schema (du/read-edn "schemas/datahike/hero-schema.edn"))

(defn hero->dh-format [{:keys [publisher] :as hero}]
  (cond-> hero publisher (update :publisher (fn [p] {:name p}))))

(comment
  (require
    '[clojure-north-2020.ch01-data.x02-hero-data :as hd]
    '[datahike.api :as d])

  (def conn (du/conn-from-dirname "tmp/hero-data-schema"))

  (d/transact conn schema)
  (count (d/transact conn (mapv hero->dh-format (hd/heroes-data))))

  (d/pull @conn '[*] [:name "Spider-Man"])

  (du/cleanup conn)
  )