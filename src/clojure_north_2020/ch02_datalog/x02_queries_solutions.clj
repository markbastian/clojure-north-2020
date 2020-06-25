(ns clojure-north-2020.ch02-datalog.x02-queries-solutions)

(comment
  (require '[datascript.core :as ds])
  (require '[clojure-north-2020.ch02-datalog.x02-queries :refer [dsdb]])
  (ds/q
    '[:find ?name ?alignment
      :in $
      :where
      [?e :name ?name]
      [?e :alignment ?alignment]]
    (ds/db-with
      dsdb
      [{:name "Joker" :alignment "Chaotic Evil"}
       {:name "Darth Vader" :alignment "Lawful Evil"}]))
  )