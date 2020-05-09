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
;;   * `{:a 1 "b" 2.0} ;A map literal with heterogeneous contents.`
;; * Sets
;;   * `#{} ;Empty set`
;;   * `#{1 2 3 :a :b :c} ;A set literal with heterogeneous contents.`
;; * Lists
;;   * `() ;Empty list`
;;   * `'(1 2 3 :a :b :c) ;A list literal with heterogeneous contents.`
;;   * Lists are evaluated and are generally not used for data modeling.
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