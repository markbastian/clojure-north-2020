(ns clojure-north-2020.ch02-datalog.x06-the-ultimate-db
  (:require [clojure-north-2020.ch01-data.x03-hero-data :as x03d]
            [clojure-north-2020.ch01-data.x04-hero-powers-data :as x04d]
            [clojure-north-2020.ch01-data.x05-supplemental-hero-data :as x05d]
            [clojure-north-2020.ch02-datalog.x03-hero-schema :as x03]
            [clojure-north-2020.ch02-datalog.x04-hero-powers-schema :as x04]
            [clojure-north-2020.ch02-datalog.x05-supplemental-hero-data-schema :as x05]
            [clojure-north-2020.ch02-datalog.datahike-utils :as du]
            [datahike.api :as d]))

;; ## Create our Final Schema
;; Now that we have our 3 data sets with their corresponding schemas we can
;; transact all of the data into one unified dataset. The schema can be combined
;; into one as shown here or each can be transacted independently as seen in the
;; comment block below.
;;
;; The truly powerful thing about this family of databases is the fact that no
;; special logic, tables, schemas, etc. were needed to join the data into a
;; unified data set. The existence of shared unique attributes provides implicit
;; joins for a very interesting data set.
(def schema
  (vec (distinct (concat x03/schema x04/schema x05/schema))))

(comment
  (def conn (du/conn-from-dirname "tmp/the-ultimate-db"))
  (count@conn)

  (d/transact conn x03/schema)
  (d/transact conn x04/schema)
  (d/transact conn x05/schema)
  (keys (d/transact conn (mapv x03/hero->dh-format (x03d/heroes-data))))
  (keys (d/transact conn (vec (x04d/powers-data))))
  (keys (d/transact conn (mapv x05/hero->dh-format (x05d/supplemental-hero-data))))
  (count@conn)

  ;; We now have a very cool set of information about our heroes, with
  ;; attributes such as teams, powers, occupations, stats, alter egos, and more.
  (d/pull @conn '[*] [:name "Spider-Man"])

  ;; Note that this will destroy the db.
  (du/cleanup conn)
  )