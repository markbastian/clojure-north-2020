(ns clojure-north-2020.ch02-datalog.x05-supplemental-hero-data-schema
  (:require [clojure-north-2020.ch02-datalog.datahike-utils :as du]))

;; ## Loading the Supplemental Hero Schema and Normalizing the Data
(def schema (du/read-edn "schemas/datahike/supplemental-hero-data-schema.edn"))

(defn add-stat-ids [{hero-name :name :as hero}]
  (letfn [(add-stat-id [stats]
            (map (fn [{stat-name :stat/name :as stat}]
                   (assoc stat :hero.stat/id (str hero-name "/" (name stat-name))))
                 stats))]
    (update hero :stats add-stat-id)))

(defn add-relative-ids [{hero-name :name :as hero}]
  (letfn [(add-relative-id [relatives]
            (map (fn [{:keys [relative] :as r}]
                   (assoc r :hero.relative/id (str hero-name "/" (:name relative))))
                 relatives))]
    (update hero :relatives add-relative-id)))

(defn add-team-affiliations [{hero-name :name :as hero}]
  (letfn [(add-team-affiliation [team-affiliation]
            (map (fn [{tn :team/name :as r}]
                   (assoc r :hero.team/id (str hero-name "/" tn)))
                 team-affiliation))]
    (update hero :team-affiliation add-team-affiliation)))

(defn creator-name->creator-ref [{:keys [creator] :as hero}]
  (cond-> hero creator (update :creator (fn [p] {:name p}))))

(defn hero->dh-format [hero]
  (-> hero
      add-stat-ids
      add-relative-ids
      add-team-affiliations
      creator-name->creator-ref))

(comment
  (require
    '[clojure-north-2020.ch01-data.x05-supplemental-hero-data :as shd]
    '[datahike.api :as d])

  (def conn (du/conn-from-dirname "tmp/supplemental-hero-data-schema"))

  (d/transact conn schema)
  (count (d/transact conn (mapv hero->dh-format (shd/supplemental-hero-data))))

  ;;Execute the following to see what data is provided by this dataset.
  (d/pull @conn '[*] [:name "Spider-Man"])

  (du/cleanup conn)
  )