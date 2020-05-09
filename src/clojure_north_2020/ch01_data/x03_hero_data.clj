;; ## Create the Primary Hero Data Set
;;
;; We are going to parse the kaggle files found at the following links:
;;
;;  *  [Super Heroes Dataset](https://www.kaggle.com/claudiodavi/superhero-set)
;; containing two files:
;;    * heroes_information.csv: Basic information on over 700 superheroes
;;    * super_hero_powers.csv: Powers attached to all superheroes.
;;  * https://www.kaggle.com/thec03u5/complete-superhero-dataset
;;
;; This exercise demonstrates the power of Clojure as a data-first language.
;; Many other languages have libraries or DSLs for processing data, but Clojure
;; is inherently a DSL for data. Data is generally loaded straight from a file,
;; normalized into a desired format, and worked with directly.
(ns clojure-north-2020.ch01-data.x03-hero-data
  (:require [clojure-csv.core :as csv]
            [clojure-north-2020.ch01-data.x02-functions :refer
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
    (->> filename
         slurp
         csv/parse-csv
         table->maps
         (map normalize))))

;; ## Exercise: Investigate Data Quality
;;
;; Determine the following:
;;
;; * We are going to treat the heroes' names as a primary key.
;;   * Do we have duplicates?
;;   * If so, what are the frequencies of the duplicates?
;; * Given a hero by name (e.g. "Spider-Man"), determine what fields are
;; duplicated.
;; Note that there are data quality issues. We are going to just accept the
;; situation and move on.
(comment
  (take 10 (heroes-data))

  ;Compute the frequency of names if the name occurs > 1 time.
  (->> (heroes-data)
       (map :name)
       frequencies
       (filter (fn [[_ v]] (> v 1)))
       (into {}))

  ;Given a hero name, determine the duplicate values associated with
  ; non-distinct keys
  (->> (heroes-data)
       (filter (comp #{"Spider-Man"} :name))
       (apply merge-with (fn [a b] (if (= a b) a (flatten (vector a b)))))
       (filter (fn [[_ v]] (seq? v)))
       (into {}))
  )
