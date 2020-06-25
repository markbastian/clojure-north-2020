(ns clojure-north-2020.ch02-datalog.x07-queries
  (:require [datahike.api :as d]))

;; ## Queries
;; We can now build a library of useful queries. Note that these are all data.
;; You can store these as edn if desired, or maintain a ns or nses of queries.
;; If care is taken, queries can be db-independent. All of these should work
;; with both datascript and datahike.

(def alignment-query
  "find the name and alignment for all entities in the db."
  '[:find ?name ?alignment
    :in $
    :where
    [?e :name ?name]
    [?e :alignment ?alignment]])

(def distinct-alignments-query
  "find the distinct alignments in db."
  '[:find [?alignment ...]
    :in $
    :where
    [_ :alignment ?alignment]])

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

;; ## Exercise - Return a set of 3-tuples (name, race, power) of all heroes in
;; the db of the same race as the input hero.
(def shared-powers-by-race-query
  [])

(def schema-query
  '[:find [(pull ?e [*]) ...]
    :in $
    :where
    [?e :db/ident ?ident]])

(def name-query
  '[:find [?name ...]
    :in $
    :where
    [?e :name ?name]])

;; ## Exercise - write a query that determines all values for attributes where
;; the type is schema. For example, what are the extant eye-colors or genders in
;; the db?
(def distinct-ident-keywords-query
  "Determine the set of valid values for each keyword (enum) type in the db."
  [])

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
           ;Fill this in above. Might want to inline for the exercise.
           shared-powers-by-race-query
           @conn hero-name)
         (group-by (fn [[n r]] [n r]))
         (map (fn [[k v]]
                (let [m (zipmap [:name :race] k)]
                  m (assoc m :powers (set (map last v))))))))

  (shared-powers-by-race "Superman")
  (shared-powers-by-race "Spider-Man")
  (shared-powers-by-race "Thor")

  ;;Given the above result, determine all powers common to Kryptonians
  ;;and Asgardians by starting with an example of one.

  ;;Note - Data quality issue.
  (:race (d/entity @conn [:name "Odin"]))
  (:race (d/entity @conn [:name "Thor"]))

  ;; We can query the db using the schemas as well. The
  ;; distinct-ident-keywords-query should return the attribute-value
  ;; combinations for all idents in the db where the type is keyword.
  (->> (d/q distinct-ident-keywords-query @conn)
       (group-by first)
       (map (fn [[k v]] [k (set (map second v))]))
       (into {}))

  (du/cleanup conn)
  )