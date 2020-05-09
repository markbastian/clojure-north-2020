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
(ns clojure-north-2020.ch01-data.x02-hero-data
  (:require [clojure-csv.core :as csv]
            [clojure-north-2020.ch01-data.x01-data :refer
             [kwize maybe-bulk-update table->maps]]))

;;; Process heroes_information.csv to get basic superhero data
(defn normalize [m]
  (let [dbl-fields [:height :weight]
        kw-fields [:gender :alignment :hair-color :skin-color :eye-color :race]]
    (-> m
        (maybe-bulk-update dbl-fields #(Double/parseDouble %))
        (maybe-bulk-update kw-fields kwize))))

(defn heroes-data []
  (let [filename "resources/heroes_information.csv"]
    (->> filename slurp csv/parse-csv table->maps (map normalize))))

;; Note that there are data quality issues. We are going to just accept the
;; situation and move on.
(comment
  (take 10 (heroes-data))

  (def dupes
    (->> (heroes-data)
         (map :name)
         frequencies
         (filter (fn [[_ v]] (> v 1)))
         (into {})))

  (def spidey-dupes
    (->> (heroes-data)
         (filter (fn [{:keys [name]}] (= name "Spider-Man")))
         (apply merge-with (fn [a b] (if (= a b) a (flatten (vector a b)))))
         (filter (fn [[_ v]] (seq? v)))
         (into {})))
  )
