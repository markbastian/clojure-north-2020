(ns clojure-north-2020.ch01-data.x03-hero-powers-data
  (:require [clojure-csv.core :as csv]
            [clojure-north-2020.ch01-data.x01-data :refer [table->maps]]))

;;; Process super_hero_powers.csv to get powers data
(defn power-reducer [raw-hero-map]
  (letfn [(f [m [k v]]
            (cond
              (= k :hero-names) (assoc m :name v)
              (= v "True") (update m :powers (comp set conj) k)
              :else m))]
    (reduce f {} raw-hero-map)))

(comment
  ;Non-reduced powers
  (let [filename "resources/super_hero_powers.csv"]
    (->> filename slurp csv/parse-csv table->maps (take 4))))

(defn powers-data []
  (let [filename "resources/super_hero_powers.csv"]
    (->> filename slurp csv/parse-csv table->maps (map power-reducer))))

(comment
  (powers-data))