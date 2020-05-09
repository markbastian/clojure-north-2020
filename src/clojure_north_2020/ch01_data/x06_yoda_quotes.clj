(ns clojure-north-2020.ch01-data.x06-yoda-quotes
  (:require [clojure-csv.core :as csv]
            [clojure-north-2020.ch01-data.x02-functions :refer [table->maps]]))

(comment
  (let [filename "resources/yoda-corpus.csv"]
    (->> filename slurp csv/parse-csv table->maps)))