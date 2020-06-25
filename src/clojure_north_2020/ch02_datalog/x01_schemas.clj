(ns clojure-north-2020.ch02-datalog.x01-schemas
  (:require [clojure-north-2020.ch02-datalog.datahike-utils :as du]
            [datahike.api :as dh]
            [datascript.core :as ds]))

;; ## Schemas
;; A key concept in data modeling with datalog databases is that schema is
;; specified at the attribute level.
;;
;; * SQL Databases: Schema is at the record level
;; * Document Databases: Documents are schema-free unless indexes are specified
;; * Datalog Databases: Schema is at the field/attribute level
;;
;; Take a moment to consider this. This is a very, very powerful concept.

;; ### Schema-free Datascript
;; Datascript is very forgiving. You can pretty much dump whatever you want into
;; a Datascript database. You get full query powers, but not all of your
;; entities explode as expected and not all queries are as efficient.
;;
;; Execute the following in a REPL. How many entities are created?
(ds/db-with
  (ds/empty-db)
  [{:name       "Batman"
    :alias      "Bruce Wayne"
    :powers     #{"Rich"}
    :weapons    #{"Utility Belt" "Kryptonite Spear"}
    :hair-color :black
    :alignment  "Chaotic Good"
    :nemesis    [{:name "Joker"}
                 {:name "Penguin"}]}
   ;Try with and without this. What happens?
   ;{:name "Batman" :alias "Bruce"}
   ])

;; ### Datascript Schemas
;; Datascript schemas are a map in which keys are the schema keys in the
;; database and the values describe the keys.
(def datascript-schema
  {:name    {:db/unique :db.unique/identity}
   :alias   {:db/unique      :db.unique/identity
             :db/cardinality :db.cardinality/many}
   :powers  {:db/cardinality :db.cardinality/many}
   :weapons {:db/cardinality :db.cardinality/many}
   :nemesis {:db/valueType   :db.type/ref
             :db/cardinality :db.cardinality/many}})

;; ### Datascript with Schema
;; Now evaluate the db with the full schema.
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
    :alias "Bruce"}])

;; What happens when a key is missing?
(ds/db-with
  (ds/empty-db
    ; Note that we're removing the alias cardinality/many attribute
    (dissoc datascript-schema :alias))
  [{:name       "Batman"
    :alias      "Bruce Wayne"
    :powers     #{"Rich"}
    :weapons    #{"Utility Belt" "Kryptonite Spear"}
    :hair-color :green
    :alignment  "Chaotic Good"
    :nemesis    [{:name "Joker"}
                 {:name "Penguin"}]}
   ; Observe that alias and hair color are overwritten
   {:name       "Batman"
    :alias      "Bruce"
    :hair-color :black}])

;; ### Datahike/Datomic Schemas
;; Datahike (and Datomic) schemas are a vector of maps in which each map
;; describes a single attribute. Unlike Datascript, a schema must exist for
;; every attribute to be transacted.
(def schema
  [{:db/ident       :name
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}

   {:db/ident       :alias
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/many}

   {:db/ident       :alignment
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :powers
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many}

   {:db/ident       :weapons
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many}

   {:db/ident       :nemesis
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many}])

(comment
  (def conn (du/conn-from-dirname "tmp/x01-schemas"))
  (dh/transact conn schema)
  ;What happens when we transact this? How do we fix it?
  ;Exercise - Fix the a schema.
  (dh/transact
    conn
    [{:name       "Batman"
      :alias      "Bruce Wayne"
      :powers     #{"Rich"}
      :weapons    #{"Utility Belt" "Kryptonite Spear"}
      :hair-color :black
      :alignment  "Chaotic Good"
      :nemesis    [{:name "Joker"}
                   {:name "Penguin"}]}
     ])
  (du/cleanup conn)
  )