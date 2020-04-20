;; ## Clojure is data
;; One of the first tasks in d
;;
;; ## The Exercise
;;
;; We are going to parse the kaggle files found at the following links:
;;
;;  *  [Super Heroes Dataset](https://www.kaggle.com/claudiodavi/superhero-set)
;; containing two files:
;;    * heroes_information.csv: Basic information on over 700 superheroes
;;    * super_hero_powers.csv: Powers attached to all superheroes.
;;  * https://www.kaggle.com/thec03u5/complete-superhero-dataset
;;
;; This exercise will demonstrate the power of Clojure as a data-first language.
;; Many other languages have libraries or DSLs for processing data, but Clojure
;; is inherently a DSL for data. One of the first things you can do is learn to
;; manipulate the basic data structures of Clojure, namely:
;;
;; * Vectors
;;   * `[] ;Empty vector`
;;   * `[1 2 3 :a :b :c] ;A vector literal with heterogeneous contents.`
;; * Map
;;   * `{} ;Empty map`
;;   * `{:a 1 "b" 2.0} ;A map literal with heterogeneous contents. Note that it is canonical to have keywords as keys.`
;; * Sets
;;   * `#{} ;Empty set`
;;   * `#{1 2 3 :a :b :c} ;A set literal with heterogeneous contents.`
;; * Lists
;;   * `() ;Empty list`
;;   * `'(1 2 3 :a :b :c) ;A list literal with heterogeneous contents. Note the tick. This quotes the list as it would otherwise be evaluated. It is not common to use list literals.`
(ns clojure-north-2020.ch01-data.x02-sample-data
  (:require [clojure-csv.core :as csv]
            [cuerdas.core :as cc]
            [clojure.string :as cs]))

;; ### Keywordize strings by:
;;  1. replacing all sequences of nonword characters with a space
;;  1. Removing all single quotes
;;  1. Turning the string into a keyword using the [Cuerdas](https://cljdoc.org/d/funcool/cuerdas/2020.03.26-3/doc/user-guide) library.
(defn kwize [s]
  (-> s (cs/replace #"\W+" " ") (cs/replace #"'" "") cc/keyword))

(defn remove-bad-entries [m]
  (into (empty m)
        (remove (fn [[k v]] (or (nil? k) (#{"-" ""} v))) m)))

(defn table->maps [[headers & cols]]
  (let [h (map cc/keyword headers)]
    (->> cols
         (map (fn [col] (zipmap h (map cs/trim col))))
         (map remove-bad-entries))))

(defn maybe-update [m k f]
  (cond-> m (m k) (update k f)))

(defn maybe-bulk-update [m ks f]
  (reduce (fn [m k] (maybe-update m k f)) m ks))

;;; Process heroes_information.csv to get basic superhero data
(defn normalize-hero-info [m]
  (let [dbl-fields [:height :weight]
        kw-fields [:gender :alignment :hair-color :skin-color :eye-color :race]]
    (-> m
        (maybe-bulk-update dbl-fields #(Double/parseDouble %))
        (maybe-bulk-update kw-fields kwize))))

(def heroes-data
  (let [filename "resources/heroes_information.csv"]
    (->> filename slurp csv/parse-csv table->maps (map normalize-hero-info))))

;;; Process super_hero_powers.csv to get powers data
(defn power-reducer [raw-hero-map]
  (letfn [(f [m [k v]]
            (cond
              (= k :hero-names) (assoc m :name v)
              (= v "True") (update m :powers (comp set conj) k)
              :else m))]
    (reduce f {} raw-hero-map)))

(def powers-data
  (let [filename "resources/super_hero_powers.csv"]
    (->> filename slurp csv/parse-csv table->maps (map power-reducer))))

;;; Process SuperheroDataset.csv to get additional source data

(defn process-team-affiliations [s]
  (let [[c f] (->> (cs/split s #"Formerly:")
                   (map (fn [s] (->> (cs/split s #",") (map cs/trim) (filter seq) set))))]
    (into
      (map (fn [t] {:current-team t}) c)
      (map (fn [t] {:former-team t}) f))))

(defn process-units-field [s]
  (let [multipliers {"ton" 1000 "meter" 100}
        [mag units] (-> s (cs/split #"//") last cs/trim (cs/split #"\s+"))]
    (* (Double/parseDouble (cs/replace mag #"," ""))
       (multipliers units 1))))

(defn process-aliases [s]
  (map (fn [a] {:alias a}) (map cs/trim (cs/split s #","))))

(defn normalize-hero-info-2 [m]
  (let [dbl-fields [:speed :intelligence :unnamed-0 :power :durability :strength :total-power :combat]
        kw-fields [:alignment :hair-color :eye-color :gender]
        unit-fields [:weight :height]]
    (-> m
        (maybe-bulk-update dbl-fields #(Double/parseDouble %))
        (maybe-bulk-update kw-fields kwize)
        (maybe-bulk-update unit-fields process-units-field)
        (maybe-update :team-affiliation process-team-affiliations)
        (maybe-update :aliases process-aliases))))
(time
  (let [filename "resources/SuperheroDataset.csv"]
    (->> filename slurp csv/parse-csv table->maps (map normalize-hero-info-2) (take 4)
         )))

(def dupes
  (->> heroes-data
       (map :name)
       frequencies
       (filter (fn [[_ v]] (> v 1)))
       (into {})))

(def spidey-dupes
  (->> (filter (fn [{:keys [name]}] (= name "Spider-Man")) heroes-data)
       (apply merge-with (fn [a b] (if (= a b) a (flatten (vector a b)))))
       (filter (fn [[_ v]] (seq? v)))
       (into {})))
