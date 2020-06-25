(ns clojure-north-2020.ch01-data.x02-functions-solutions
  (:require [clojure-north-2020.ch01-data.x01-data-solutions :refer [data]]
            [clojure.string :as cs]
            [cuerdas.core :as cc]))

(comment
  (->> data
       (filter (fn [{:keys [powers]}] (get powers "Bullet Immunity")))
       (mapcat :nemesis))

  ;; Find the alias of the nemesis of everyone with an alias
  (let [d (->> data (filter :alias) (mapcat :nemesis) (map :name) set)]
    (->> data
         (filter (fn [{:keys [name]}] (d name)))
         (map :alias)))
  )

;; Remove entries in a seq of pairs for which any of the following are true:
;; * The first item (key) is nil
;; * The second item (value) is nil, "", or "-"
(defn remove-bad-entries [m]
  (into (empty m)
        (remove (fn [[k v]] (or (nil? k) (contains? #{"-" "" nil} v))) m)))

(comment
  (remove-bad-entries
    {nil "A" :a nil :b "" :c "OK"}))

;; Convert a sequence of vectors into a sequence of maps, assuming the first row
;; of the vectors is a header row
(defn table->maps [[headers & cols]]
  (let [h (map cc/keyword headers)]
    (->> cols
         (map (fn [col] (zipmap h (map #(cond-> % (string? %) cs/trim) col))))
         (map remove-bad-entries))))