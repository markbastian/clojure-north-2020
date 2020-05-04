(ns clojure-north-2020.ch02-datalog.x03-datahike-batman
  (:require [clojure-north-2020.ch01-data.x01-data :refer [data]]
            [clojure.java.io :as io]
            [datahike.api :as d]))

;; ## Datahike Schemas
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
  (let [db-dir (doto
                 (io/file "tmp/batman")
                 io/make-parents)]
    (def uri (str "datahike:" (io/as-url db-dir))))

  (when-not (d/database-exists? uri)
    (d/create-database uri))
  (def conn (d/connect uri))
  (d/transact conn schema)
  (d/transact conn data)

  (d/q
    '[:find ?name .
      :in $ ?enemy-name
      :where
      [?e :name ?name]
      [?e :nemesis ?n]
      [?n :name ?enemy-name]]
    @conn "Joker")

  (d/transact conn [{:name "Joker"
                     :alignment "Chaotic Evil"}
                    {:name "Penguin"
                     :alignment "Neutral Evil"}])

  (d/q
    '[:find ?name ?alignment
      :in $
      :where
      [?e :name ?name]
      [?e :alignment ?alignment]]
    @conn)
  )

;; Clean everything up
(comment
  (do
    (d/release conn)
    (d/delete-database uri)))

