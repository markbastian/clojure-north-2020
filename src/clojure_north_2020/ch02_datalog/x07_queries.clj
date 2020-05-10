(ns clojure-north-2020.ch02-datalog.x07-queries
  (:require [datahike.api :as d]))

;; ## Queries
;; We can now build a library of useful queries. Note that these are all data.
;; You can store these as edn if desired, or maintain a ns or nses of queries.
;; If care is taken, queries can be db-independent. All of these should work
;; with both datascript and datahike.

(def alignment-query
  "find the and alignment for all entities in the db."
  '[:find ?name ?alignment
    :in $
    :where
    [?e :name ?name]
    [?e :alignment ?alignment]])

(def nemeses-query
  "find the nemeses of a given hero."
  '[:find [?nemesis-name ...]
    :in $ ?name
    :where
    [?e :name ?name]
    [?e :nemesis ?nemesis]
    [?nemesis :name ?nemesis-name]])

(def shared-powers-query
  "Find powers shared between this super and others"
  '[:find ?that-name ?power
    :in $ ?this-name
    :where
    [?e :name ?this-name]
    [?e :powers ?power]
    [?f :powers ?power]
    [?f :name ?that-name]
    [(not= ?e ?f)]])

(def powers-by-count-query
  "Return all supers by name and power count"
  '[:find ?name (count ?powers)
    :in $
    :where
    [?e :name ?name]
    [?e :powers ?powers]])

(def shared-powers-by-race-query
  '[:find ?name ?race ?powers
    :in $ ?n
    :where
    [?e :name ?n]
    [?e :race ?race]
    [?e :powers ?powers]
    [?f :race ?race]
    [?f :name ?name]
    [?f :powers ?powers]
    [(not= ?e ?f)]])

(comment
  (require '[clojure-north-2020.ch02-datalog.datahike-utils :as du]
           '[datahike.api :as d]
           '[clojure.set :refer [intersection]])

  ;;Use the previous "ultimate" db (don't clean it up yet).
  (def conn (du/conn-from-dirname "tmp/the-ultimate-db"))
  ;;Reality check
  (count @conn)

  ;; We now have a very cool set of information about our heroes, with
  ;; attributes such as teams, powers, occupations, stats, alter egos, and more.
  (d/pull @conn '[*] [:name "Spider-Man"])
  (d/pull @conn '[*] [:name "Odin"])
  (d/pull @conn '[*] [:name "Thor"])
  (d/pull @conn '[*] [:name "Spectre"])
  (d/pull @conn '[*] [:name "Superman"])
  (d/pull @conn '[*] [:name "Faora"])

  (->> (d/q powers-by-count-query @conn)
       (sort-by second))

  ;;Exercise: Determine the shared powers of heroes of a given race using only
  ;; their name. Using this query, determine the powers of Kryptonians.
  (defn shared-powers-by-race [hero-name]
    (->> (d/q
           shared-powers-by-race-query
           @conn hero-name)
         (group-by (fn [[n r]] [n r]))
         (map (fn [[k v]]
                (let [m (zipmap [:name :race] k)]
                  m (assoc m :powers (set (map last v))))))))

  (shared-powers-by-race "Superman")
  (shared-powers-by-race "Spider-Man")
  (shared-powers-by-race "Thor")

  ;;Given the above result, determine all powers common to this race
  (->> "Superman"
       shared-powers-by-race
       (map :powers)
       (apply intersection))

  (->> "Thor"
       shared-powers-by-race
       (map :powers)
       (apply intersection))

  (:race (d/entity @conn [:name "Odin"]))
  (:race (d/entity @conn [:name "Thor"]))

  (du/cleanup conn)
  )