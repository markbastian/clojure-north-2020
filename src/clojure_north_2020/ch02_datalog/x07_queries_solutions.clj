(ns clojure-north-2020.ch02-datalog.x07-queries-solutions)

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
  ;;All powers shared by Kryptonians
  (->> "Superman"
       shared-powers-by-race
       (map :powers)
       (apply intersection))

  ;;All powers shared by Asgardians
  (->> "Thor"
       shared-powers-by-race
       (map :powers)
       (apply intersection))

  (def distinct-ident-keywords-query
    "Determine the set of valid for each keyword (enum) type in the db."
    '[:find ?ident ?v
      :in $
      :where
      [?e :db/ident ?ident]
      [?e :db/valueType :db.type/keyword]
      [_ ?ident ?v]])
  )