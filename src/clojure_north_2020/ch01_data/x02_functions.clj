(ns clojure-north-2020.ch01-data.x02-functions
  (:require [clojure-csv.core :as csv]
            [clojure-north-2020.ch01-data.x01-data :refer [data]]
            [clojure-north-2020.ch01-data.x02-functions-solutions :as x02-soln]
            [clojure.string :as cs]
            [cuerdas.core :as cc]))

;; ## Manipulating Data and Building APIs with Functions
;; As a language, Clojure is great at working with data. It's very common once
;; you establish a data model to work with it interactively to build domain API
;; functions or just better understand the data. As you explore and find useful
;; relations it is common to "lift" a form into a function.
;;
;; ### Exercise: Interact with our data
;; Explore your data and write a useful function. Examples:
;; * Find everyone with a given power.
;; * List the names of all characters that are the nemesis of someone with a
;; given power.
;; * Find the alias of the nemesis of everyone with an alias.
;; * Lift one of these explorations to a function.

;; ## Some Utility Functions
;; We will be reading in our data from csv files. Let's write some functions for
;; converting our raw files into our data as we've modeled it previously. One of
;; the great things about Clojure is that is effectively a DSL for data, so the
;; functions we create now will be domain agnostic and of general utility in the
;; future since we're just dealing with data.
;;
;; ### Exercise: Write a csv->data function
;; * Write a function that consumes a csv file and produces a sequence of maps
;;   for each entry in the file.
;; * Modify the function to convert column names to keywords. Tip: Clojure
;;   has the keyword function and the cuerdas library (included) has an even
;;   better one.
;; * Modify the function to trim string values.
;; * Modify the function to remove "garbage" values. If a key is empty or nil or
;;   a value is empty, nil, or "-", remove it from the map.
;;
;; Step 1: Inspect the data. We can see that the first row is the column names
;; and the rest are data. We also see some data quality issues:
;; * The first column (index?) has no name
;; * Nonexistent values are "-"
;; * Nothing is done to parse non-string values
(comment
  (->> "resources/heroes_information.csv"
       slurp
       csv/parse-csv
       ;Just look a a sampling of the data
       (take 4)))

;; Remove entries in a seq of pairs for which any of the following are true:
;; * The first item (key) is nil
;; * The second item (value) is nil, "", or "-"
(defn remove-bad-entries [m]
  ;TODO - implement
  )

(comment
  (= {:c "OK"}
     (remove-bad-entries {nil "A" :a nil :b "" :c "OK"}))
  (= [[:c "OK"]]
     (remove-bad-entries [[nil "A"] [:a nil] [:b ""] [:c "OK"]])))

;; Convert a sequence of vectors into a sequence of maps, assuming the first row
;; of the vectors is a header row
(defn table->maps [[headers & cols]]
  ;TODO - implement
  )

(comment
  (= [{:id 1 :name "Mark" :age 42}
      {:id 2 :name "Sue" :age 12 :phone "123-456-7890"}
      {:id 3 :name "Pat" :age 18}]
     (table->maps
       [["" "ID" "Name" "Age" "Phone"]
        [0 1 "Mark" 42 "-"]
        [1 2 "Sue" 12 "123-456-7890"]
        [2 3 "Pat" 18 nil]])))

(defn csv-file->maps [f]
  (-> f
      slurp
      csv/parse-csv
      ;Once you get your solution in place, remove the external solution
      x02-soln/table->maps))

;; ### Other utility functions
;; This won't be an exercise, but here are a few more utility functions that we
;; will be using to parse our data.

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