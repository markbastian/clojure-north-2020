(ns clojure-north-2020.ch02-datalog.x01-schemas)

;; ## Schemas
;; A key concept in data modeling with datalog databases is that schema is
;; specified at the attribute level.
;;
;; * SQL Databases: Schema is at the record level
;; * Document Databases: Documents are schema-free unless indexes are specified
;; * Datalog Databases: Schema is at the field/attribute level
;;
;; Take a moment to consider this. This is a very, very powerful concept.

;; ## Datascript Schemas
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

;; ## Datahike/Datomic Schemas
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