(ns clojure-north-2020.ch04-application.queries
  (:require [datahike.api :as d]))

(def characters-with-alignment-and-hair-color-query
  '[:find [?name ...]
    :in $ ?alignment ?color
    :where
    [?e :name ?name]
    [?e :alignment ?alignment]
    [?e :hair-color ?color]])

(def characters-with-alignment-and-hair-color
  (partial d/q characters-with-alignment-and-hair-color-query))

(def alignment-hair-color-universe-query
  '[:find [?name ...]
    :in $ ?alignment ?color ?publisher
    :where
    [?e :name ?name]
    [?e :publisher ?publisher]
    [?e :alignment ?alignment]
    [?e :hair-color ?color]])

(def alignment-hair-color-universe
  (partial d/q alignment-hair-color-universe-query))

(def common-colors-query
  '[:find ?name ?color ?publisher ?alignment
    :in $
    :where
    [?e :name ?name]
    [?e :publisher ?p]
    [?p :name ?publisher]
    [?e :alignment ?alignment]
    [?e :skin-color ?color]
    [?e :eye-color ?color]
    [?e :hair-color ?color]])

(def common-colors (partial d/q common-colors-query))

(def monocolor-query
  '[:find (pull ?e [* {:publisher [:name]}])
    :in $
    :where
    [?e :name ?name]
    [?e :skin-color ?color]
    [?e :eye-color ?color]
    [?e :hair-color ?color]
    [(not= ?a1 ?a2)]
    [(not= ?a2 ?a3)]
    [(not= ?a3 ?a1)]])

(def monocolor (partial d/q monocolor-query))
