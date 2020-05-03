(ns clojure-north-2020.ch02-datalog.x04-supplemental-hero-data-schema
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(def schema
  (->> (io/resource "schemas/supplemental-hero-data-schema.edn")
       slurp
       edn/read-string))

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
  (cond-> hero creator (update hero :creator (fn [p] {:name p}))))

(defn hero->dh-format [hero]
  (-> hero
      add-stat-ids
      add-relative-ids
      add-team-affiliations
      creator-name->creator-ref))

(comment
  (require
    '[clojure-north-2020.ch01-data.x04-supplemental-hero-data :as shd]
    '[datahike.api :as d])

  ;Create the db
  (let [db-dir (doto
                 (io/file "tmp/supplemental-hero-data-schema")
                 io/make-parents)]
    (def uri (str "datahike:" (io/as-url db-dir))))

  (when-not (d/database-exists? uri)
    (d/create-database uri))

  (def conn (d/connect uri))

  (d/transact conn schema)
  (count (d/transact conn (mapv hero->dh-format (shd/supplemental-hero-data))))

  (d/pull @conn '[*] [:name "Spider-Man"])

  (do
    (d/release conn)
    (d/delete-database uri))
  )