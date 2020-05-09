(ns clojure-north-2020.ch02-datalog.x06-schema
  (:require [clojure-north-2020.ch01-data.x03-hero-data :as data]
            [clojure-north-2020.ch01-data.x04-hero-powers-data :as hp]
            [clojure-north-2020.ch01-data.x05-supplemental-hero-data :as shp]
            [clojure-north-2020.ch04-application.parts.datahike :as dh]
            [clojure-north-2020.ch04-application.queries :as queries]
            [clojure.java.io :as io]
            [datahike.api :as d]
            [integrant.core :as ig]))

(def schema
  [{:db/ident       :name
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}

   {:db/ident       :url
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}

   {:db/ident       :publisher
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many}

   {:db/ident       :creator
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many}

   {:db/ident       :team-affiliation
    :db/valueType   :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/many}

   {:db/ident       :aliases
    :db/valueType   :db.type/string
    :db/isComponent true
    :db/cardinality :db.cardinality/many}

   {:db/ident       :team/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :first-appearance
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident       :team/leader?
    :db/valueType   :db.type/boolean
    :db/cardinality :db.cardinality/one}

   {:db/ident       :team/former?
    :db/valueType   :db.type/boolean
    :db/cardinality :db.cardinality/one}

   {:db/ident       :occupation
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many}

   {:db/ident       :base
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many}

   {:db/ident       :full-name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many}

   {:db/ident       :place-of-birth
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many}

   {:db/ident       :stats
    :db/valueType   :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/many}

   {:db/ident       :hero.stat/name
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}

   {:db/ident       :hero.relative/id
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}

   {:db/ident       :hero.team/id
    :db/valueType   :db.type/string
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}

   {:db/ident       :stat/name
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident       :stat/value
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   {:db/ident       :relatives
    :db/valueType   :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/many}

   {:db/ident       :relative
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one}

   {:db/ident       :relationship
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident       :alter-egos
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many}

   {:db/ident       :race
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident       :alignment
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident       :hair-color
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident       :eye-color
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident       :skin-color
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident       :gender
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one}

   {:db/ident       :height
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   {:db/ident       :weight
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one}

   ;;Added to join powers-data
   {:db/ident       :powers
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/many}])

(defn tx-hero [{:keys [publisher] :as hero}]
  (cond-> hero publisher (update :publisher (fn [p] {:name p}))))

(defn tx-hero-2 [{:keys [creator stats relatives name] :as hero}]
  (-> hero
      (update :stats
              (fn [stats]
                (map (fn [{stat-name :stat/name :as stat}]
                       (assoc stat :hero.stat/name (str name "/" stat-name)))
                     stats)))
      (update :relatives
              (fn [relatives]
                (map (fn [{:keys [relative] :as r}]
                       (assoc r :hero.relative/id (str name "/" (:name relative))))
                     relatives)))
      (update :team-affiliation
              (fn [team-affiliation]
                (map (fn [{tn :team/name :as r}]
                       (assoc r :hero.team/id (str name "/" tn)))
                     team-affiliation)))
      (cond-> creator (update :creator (fn [p] {:name p})))))

(def config {::dh/database   {:db-file         "tmp/heroes"
                              :initial-tx      schema
                              :delete-on-halt? true}
             ::dh/connection {:db-config (ig/ref ::dh/database)}})

(comment
  (def c (ig/init config))

  (def tx
    (let [{:keys [::dh/connection]} c]
      (d/transact! connection (mapv tx-hero (data/heroes-data)))
      (d/transact! connection (mapv tx-hero (hp/powers-data)))
      (d/transact! connection (mapv tx-hero-2 (shp/supplemental-hero-data)))))

  (let [{:keys [::dh/connection]} c
        db @connection]
    (count db))

  (let [{:keys [::dh/connection]} c
        db @connection]
    (d/pull db '[*] [:name "Spider-Man"]))

  (let [{:keys [::dh/connection]} c
        db @connection]
    (d/q
      '[?e
        :in $ ?e
        :where
        []]
      db
      [:name "Spider-Man"]))

  (let [{:keys [::dh/connection]} c
        db @connection]
    (queries/characters-with-alignment-and-hair-color db :good :red))

  (let [{:keys [::dh/connection]} c
        db @connection]
    (queries/characters-with-alignment-and-hair-color db :bad :red))

  (let [{:keys [::dh/connection]} c
        db @connection]
    (queries/alignment-hair-color-universe db :bad :red [:name "Marvel Comics"]))

  (let [{:keys [::dh/connection]} c
        db @connection]
    (queries/common-colors db))

  (let [{:keys [::dh/connection]} c
        db @connection]
    (->> (queries/monocolor db)
         flatten
         (map #(select-keys % [:name :alignment :skin-color]))))


  (ig/halt! c)

  )

(comment
  (let [db-dir (doto
                 (io/file "tmp/heroes")
                 io/make-parents)]
    (def uri (str "datahike:" (io/as-url db-dir))))

  (when-not (d/database-exists? uri) (d/create-database uri))
  (def conn (d/connect uri))
  ;(doseq [tx-data [schema races genders alignments colors]]
  ;  (d/transact conn tx-data))
  (d/transact conn schema)
  (d/transact conn (mapv tx-hero data/heroes-data))
  (d/transact conn (mapv tx-hero data/powers-data))

  (d/transact conn [{:name      "Joker"
                     :alignment "Chaotic Evil"}
                    {:name      "Penguin"
                     :alignment "Neutral Evil"}])

  (d/q
    '[:find ?name ?a
      :in $
      :where
      [?e :name ?name]
      [?e :alignment ?alignment]
      [?alignment :db/ident ?a]]
    @conn)

  (group-by first
            (d/q
              '[:find ?name ?c
                :in $ ?a ?b
                :where
                [?e :name ?name]
                [?e :powers ?a]
                [?e :powers ?b]
                [?e :powers ?c]]
              @conn :time-travel :agility))

  (->> (d/q
         '[:find ?name (count ?powers)
           :in $
           :where
           [?e :name ?name]
           [?e :powers ?powers]]
         @conn)
       (sort-by second))

  (d/pull @conn '[*] [:name "Odin"])
  (d/pull @conn '[*] [:name "Spectre"])
  )

;; Clean everything up
(comment
  (do
    (d/release conn)
    (d/delete-database uri)))
