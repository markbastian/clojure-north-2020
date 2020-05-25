(ns clojure-north-2020.ch01-data.x06-yoda-quotes
  (:require [clojure-csv.core :as csv]
            [clojure-north-2020.ch01-data.x02-functions :refer [csv-file->maps]]))

(comment
  (csv-file->maps "resources/yoda-corpus.csv"))