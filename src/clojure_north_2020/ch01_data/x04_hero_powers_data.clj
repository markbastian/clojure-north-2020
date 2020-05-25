(ns clojure-north-2020.ch01-data.x04-hero-powers-data
  (:require [clojure-csv.core :as csv]
            [clojure-north-2020.ch01-data.x02-functions :refer [csv-file->maps]]))

;; ## Get Powers Data
;;
;;; We'll process super_hero_powers.csv to get powers data.
;;
;; Convert map of {:hero-name "name" :powerx "True|False"} to map of
;; {:name "name" :powers #{:set :of :powers}}.
(defn normalize [raw-hero-map]
  (letfn [(f [m [k v]]
            (cond
              (= k :hero-names) (assoc m :name v)
              (= v "True") (update m :powers (comp set conj) k)
              :else m))]
    (reduce f {} raw-hero-map)))

;; Note that the dataset is a seq of maps with the key :hero-names (the name)
;; and a boolean (as string) key for each power. Write a function that
;; normalizes our data into a map with the hero's name as :name and powers as a
;; set of keywords (e.g. #{:super-strength :flight}).
(defn powers-data []
  (let [filename "resources/super_hero_powers.csv"]
    (->> filename
         csv-file->maps
         (map normalize))))

(comment
  ;Non-reduced powers
  (let [filename "resources/super_hero_powers.csv"]
    (->> filename slurp csv/parse-csv table->maps (take 4)))

  (take 2 (powers-data))

  (->> (powers-data)
       (filter (fn [{:keys [name powers]}]
                 (and (powers :flight)
                      (powers :super-strength)
                      (powers :vision-x-ray))))
       (map :name))
  )