(ns clojure-north-2020.ch01-data.x01-data
  (:require [clojure.string :as cs]
            [cuerdas.core :as cc]))

;; ## A Clojure Application Starts as Data
;; Perhaps the most overlooked and powerful aspect of Clojure is that it is a
;; first-class data oriented language. That, combined with functional
;; programming, sets it aside from nearly any other language.
;;
;; In this namespace, were going to consider the first step you should undertake
;; when writing any Clojure program - modeling your domain as data.
;;
;; Forget your functions, code, etc. Instead just think about your domain. How
;; might you model it as data?
;;
;; In Clojure you have 4 key data structures:
;;
;; * Vectors
;;   * `[] ;Empty vector`
;;   * `[1 2 3 :a :b :c] ;A vector literal with heterogeneous contents.`
;; * Map
;;   * `{} ;Empty map`
;;   * `{:a 1 "b" 2.0} ;A map literal with heterogeneous contents.`
;; * Sets
;;   * `#{} ;Empty set`
;;   * `#{1 2 3 :a :b :c} ;A set literal with heterogeneous contents.`
;; * Lists
;;   * `() ;Empty list`
;;   * `'(1 2 3 :a :b :c) ;A list literal with heterogeneous contents.`
;;
;; Given these simple literal data structures you should be able to model any
;; problem in any domain. Model actual cases. This is a far more powerful
;; technique than starting with a schema or class hierarchy.
;;
;; Consider how you might model the following domains:
;;
;; * [A Role-Playing Game Character](https://ericlippert.com/2015/04/27/wizards-and-warriors-part-one/)
;; * Super Heroes
;;
;; For this project, we'll be modeling superheroes. Consider how you might model
;; a superhero with attributes such as name, alias, powers, weapons, etc.
;;
;; One interesting aspect of this problem is how we reference other entities,
;; such as villains/nemeses. These are also top-level items, so perhaps it makes
;; sense to ID them by name vs. a plain old string.
;;
(def data
  [{:name      "Batman"
    :alias     "Bruce Wayne"
    :powers    #{"Rich"}
    :weapons   #{"Utility Belt" "Kryptonite Spear"}
    :alignment "Chaotic Good"
    :nemesis   [{:name "Joker"}
                {:name "Penguin"}]}
   ;;TODO - Add more supers
   {:name      "Superman"
    :alias     "Clark Kent"
    :powers    #{"Strength" "Flight" "Bullet Immunity"}
    :alignment "Lawful Good"
    :nemesis   [{:name "Lex Luthor"}
                {:name "Zod"}
                {:name "Faora"}]}
   {:name      "Wonder Woman"
    :alias     "Diana Prince"
    :powers    #{"Strength" "Flight"}
    :weapons   #{"Lasso of Truth" "Bracers"}
    :alignment "Lawful Good"
    :nemesis   [{:name "Ares"}]}
   {:name      "Shazam"
    :alias     "Billy Batson"
    :powers    #{"Strength" "Bullet Immunity"}
    :alignment "Neutral Good"
    :nemesis   [{:name "Dr. Thaddeus Sivana"}
                {:name "Pride"}
                {:name "Envy"}
                {:name "Greed"}
                {:name "Wrath"}
                {:name "Sloth"}
                {:name "Gluttony"}
                {:name "Lust"}]}
   ;TODO - Add villains
   {:name      "Joker"
    :alias     "Jack Napier"
    :alignment "Chaotic Evil"
    :nemesis   [{:name "Batman"}]
    }
   ])

;; ## Working with our data
;; Find the names of the nemesis of everyone with bullet immunity
(comment
  (->> data
       (filter (fn [{:keys [powers]}] (get powers "Bullet Immunity")))
       (mapcat :nemesis))

  ;Find the alias of the nemesis of everyone with an alias
  (let [d (->> data (filter :alias) (mapcat :nemesis) (map :name) set)]
    (->> data
         (filter (fn [{:keys [name]}] (d name)))
         (map :alias)))
  )

;; ## Clojure works with Data
;; As a language, Clojure is great at working with data. Here are some functions
;; we'll be using in the next few namespaces to parse and normalize our data.
;;
;; Remove entries in a seq of pairs for which any of the following are true:
;; * The first item (key) is nil
;; * The second item (value) is nil, "", or "-"
(defn remove-bad-entries [m]
  (into (empty m)
        (remove (fn [[k v]] (or (nil? k) (contains? #{"-" "" nil} v))) m)))

(comment
  (remove-bad-entries
    {nil "A" :a nil :b "" :c "OK"})
  )

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