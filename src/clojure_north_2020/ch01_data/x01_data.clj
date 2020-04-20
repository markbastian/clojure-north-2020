(ns clojure-north-2020.ch01-data.x01-data)

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
;;   * `{:a 1 "b" 2.0} ;A map literal with heterogeneous contents. Note that it is canonical to have keywords as keys.`
;; * Sets
;;   * `#{} ;Empty set`
;;   * `#{1 2 3 :a :b :c} ;A set literal with heterogeneous contents.`
;; * Lists
;;   * `() ;Empty list`
;;   * `'(1 2 3 :a :b :c) ;A list literal with heterogeneous contents. Note the tick. This quotes the list as it would otherwise be evaluated. It is not common to use list literals.`
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
(def data
  [{:name      "Batman"
    :alias     "Bruce Wayne"
    :powers    ["Rich"]
    :weapons   ["Belt" "Kryptonite Spear"]
    :alignment "Chaotic Good"
    :nemesis   [{:name "Joker"}
                {:name "Penguin"}]}])
