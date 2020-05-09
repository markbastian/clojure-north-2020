(ns clojure-north-2020.ch02-datalog.x02-queries
  (:require [clojure-north-2020.ch02-datalog.x01-schemas :refer [datascript-schema]]
            [datascript.core :as ds]
            [datahike.api :as d]))

;; ## Queries and Data Access
;; In this section we discuss the key ways to get data from a db.
;; For Datascript and Datahike the APIs are the same except for time-travel
;; functions. Other features are not identical across all implementations.
;; For example, the entity API is not available in Datomic Cloud and as-of is
;; not avaiable for Datascript.
;;
;; We'll just use Datascript for this example as the APIs are identical for what
;; is being demonstrated.
(def dsdb
  (ds/db-with
    (ds/empty-db
      datascript-schema)
    [{:name       "Batman"
      :alias      "Bruce Wayne"
      :powers     #{"Rich"}
      :weapons    #{"Utility Belt" "Kryptonite Spear"}
      :hair-color :black
      :alignment  "Chaotic Good"
      :nemesis    [{:name "Joker"}
                   {:name "Penguin"}]}
     {:name  "Batman"
      :alias "Bruce"}]))

;; ### The Pull API
;; This allows you to 'pull' facts straight from a db given an identity.
(ds/pull dsdb '[*] 1)
;; You can use 'lookup refs' for any unique identity (entity or value).
(ds/pull dsdb '[*] [:name "Batman"])
;; You can specify certain keys as well as do more complex attribute specs.
(ds/pull dsdb '[:name {:nemesis [:name]}] [:name "Batman"])

;; ### The Entity API
;; When you get an entity it's like situating yourself at a node in a graph db.
(:name (ds/entity dsdb [:name "Batman"]))
;; You can navigate any way you want. This is a forward reference.
(->> (ds/entity dsdb [:name "Batman"])
     :nemesis)
;; You can also do 'backrefs' to walk the link backwards.
(->> (ds/entity dsdb [:name "Joker"])
     :_nemesis)

;; ### The Query API
;; This is the most powerful and most commonly used API.
;;
;; Queries have a powerful datalog syntax. Note that queries are data. It is
;; common to inline them with the q function, but they can be stored up as
;; standalone items in a file, db, etc.
(def nemeses-query
  '[:find [?enemy-name ...]
    :in $ ?name
    :where
    [?e :name ?name]
    [?e :nemesis ?n]
    [?n :name ?enemy-name]])

(ds/q nemeses-query dsdb "Batman")

;; Exercise - Write a query to list the name and alignment of all individuals
;; in the database.
(ds/q
  ;TODO - Blank out
  '[:find ?name ?alignment
    :in $
    :where
    [?e :name ?name]
    [?e :alignment ?alignment]]
  (ds/db-with
    dsdb
    [{:name "Joker" :alignment "Chaotic Evil"}
     {:name "Darth Vader" :alignment "Lawful Evil"}]))
