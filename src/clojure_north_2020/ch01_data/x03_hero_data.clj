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
             [csv-file->maps kwize maybe-bulk-update]]))

;;; Process heroes_information.csv to get basic superhero data
(defn normalize [m]
  (let [dbl-fields [:height :weight]
        kw-fields [:gender :alignment :hair-color :skin-color :eye-color :race]]
    (-> m
        (maybe-bulk-update dbl-fields #(Double/parseDouble %))
        (maybe-bulk-update kw-fields kwize))))

(comment
  (normalize
    {:publisher  "DC Comics"
     :race       "Martian"
     :name       "Martian Manhunter"
     :alignment  "good"
     :weight     "135.0"
     :hair-color "No Hair"
     :skin-color "green"
     :eye-color  "red"
     :gender     "Male"
     :height     "201.0"}))

(defn heroes-data []
  (let [filename "resources/heroes_information.csv"]
    (->> filename
         csv-file->maps
         (map normalize))))

;; ## Exercise: Investigate Data Quality
;;
;; Determine the following:
;;
;; * We are going to treat the heroes' names as a primary key.
;;   * Do we have duplicates?
;;   * If so, what are the frequencies of the duplicates?
;; * Given a hero by name (e.g. "Spider-Man"), determine what fields are
;; duplicated and what the distinct values are.
;; Note that there are data quality issues. We are going to just accept the
;; situation and move on.
(comment
  (take 10 (heroes-data))

  ;Compute the frequency of names if the name occurs > 1 time.
  (->> (heroes-data)
       ;...
       )

  ;Given a hero name, determine the duplicate values associated with
  ; non-distinct keys. You may want to use the test data from below.
  (->> (heroes-data)
       ;...
       )

  ; Hint:
  (do
    (use 'clojure.repl)
    (doc merge-with))

  ;TODO - Promote to the function "dupes"
  (defn dupes [maps]
    ;Extract by promoting the previous exercise
    )

  ;We've build a generally useful function
  (= {:age [12 14 16]
      :height [100 101]}
     (dupes
       [{:name "Mark" :age 12}
        {:name "Mark" :age 12 :height 100}
        {:name "Mark" :age 14 :height 100}
        {:name "Mark" :age 16 :height 101}]))
  )
