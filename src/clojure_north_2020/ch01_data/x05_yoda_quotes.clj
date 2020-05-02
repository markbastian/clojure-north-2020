(ns clojure-north-2020.ch01-data.x05-yoda-quotes
  (:require [clojure-csv.core :as csv]
            [clojure-north-2020.ch01-data.x01-data :refer [table->maps]]))

(let [filename "resources/yoda-corpus.csv"]
  (->> filename slurp csv/parse-csv table->maps))