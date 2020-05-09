(ns clojure-north-2020.ch02-datalog.x00-basics
  (:require [datascript.core :as d]))

;; ## Datascript (and Datahike, Datomic, and Crux) are Fact Stores
(def db
  (d/db-with
    (d/empty-db)
    [{:name "Mark" :favorite-food "Pizza"}
     {:name "Pat" :favorite-food "Ice Cream" :age 42}
     {:name "Chloe" :favorite-food "Chips"}]))

;; ## "Facts" are Datoms
;; Evaluate the above code in a REPL. `db` explodes to the following set of
;; entries:
[[1 :favorite-food "Pizza" 536870913]
 [1 :name "Mark" 536870913]
 [2 :age 42 536870913]
 [2 :favorite-food "Ice Cream" 536870913]
 [2 :name "Pat" 536870913]
 [3 :favorite-food "Chips" 536870913]
 [3 :name "Chloe" 536870913]]

;; These entries are `[E A V T]` Datoms where:
;;
;; * E is the Entity ID of an entity in the database
;; * A is the Attribute being described
;; * V is the Value associated with the attribute
;; * T is the Transaction ID or Time associated with the tuple
;;
;; Datoms are often called "Facts" and the DBs themselves considered
;; "Fact Stores." In plain terms, a fact is a statement made about something for
;; some point in time and a datom describes such facts.
;;
;; Note that some implementations may also have additional fields such as a
;; boolean indicating whether the fact was asserted or retracted.

;; ## Queries
;; Queries are done by unifying facts
(d/q
  '[:find ?n
    :in $
    :where
    [?e :name ?n]]
  db)