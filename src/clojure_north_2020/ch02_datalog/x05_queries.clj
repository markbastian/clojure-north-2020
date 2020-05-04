(ns clojure-north-2020.ch02-datalog.x05-queries)

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
  "Find powers "
  '[:find ?that-name ?power
    :in $ ?this-name
    :where
    [?e :name ?this-name]
    [?e :powers ?power]
    [?f :powers ?power]
    [?f :name ?that-name]
    [(not= ?e ?f)]])