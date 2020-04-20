(ns clojure-north-2020.ch02-datalog.x05-queries)

(def alignment-query
  "find the and alignment for all entities in the db."
  '[:find ?name ?alignment
    :in $
    :where
    [?e :name ?name]
    [?e :alignment ?alignment]])