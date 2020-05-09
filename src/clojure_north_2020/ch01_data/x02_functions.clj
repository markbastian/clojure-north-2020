(ns clojure-north-2020.ch01-data.x02-functions
  (:require [clojure-north-2020.ch01-data.x01-data :refer [data]]
            [clojure.string :as cs]
            [cuerdas.core :as cc]))

;; ## Manipulating Data and Building APIs with Functions
;; As a language, Clojure is great at working with data. It's very common once
;; you establish a data model to work with it interactively to build domain API
;; functions or just better understand the data.
;;
;; ### Interact with our data
;; Find the names of the nemesis of everyone with bullet immunity
(comment
  (->> data
       (filter (fn [{:keys [powers]}] (get powers "Bullet Immunity")))
       (mapcat :nemesis))

  ;; Find the alias of the nemesis of everyone with an alias
  (let [d (->> data (filter :alias) (mapcat :nemesis) (map :name) set)]
    (->> data
         (filter (fn [{:keys [name]}] (d name)))
         (map :alias))))

;; ## Some Utility Functions
;; Here are some functions we'll be using in the next few namespaces to parse
;; and normalize our data as it is proviced as csv.
;;
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

(comment
  (table->maps
   [["ID" "Name" "Age" "Phone"]
    [1 "Mark" 42 "-"]
    [2 "Sue" 12 "123-456-7890"]
    [3 "Pat" 18 nil]]))

;; ### Keywordize strings by:
;;  1. replacing all sequences of nonword characters with a space
;;  1. Removing all single quotes
;;  1. Turning the string into a keyword using the 
;;  [Cuerdas](https://cljdoc.org/d/funcool/cuerdas/2020.03.26-3/doc/user-guide) library.
(defn kwize [s]
  (-> s (cs/replace #"\W+" " ") (cs/replace #"'" "") cc/keyword))

;;Update key k in map m with function f if there is a value in m for k.
(defn maybe-update [m k f]
  (cond-> m (some? (m k)) (update k f)))

;;Bulk update several keys ks in map m with function f
(defn maybe-bulk-update [m ks f]
  (reduce (fn [m k] (maybe-update m k f)) m ks))

(comment
  (maybe-bulk-update
   {:width "2.1" :height "45.53" :name "Bob"}
   [:width :height]
   #(Double/parseDouble %)))