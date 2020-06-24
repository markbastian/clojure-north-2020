(ns clojure-north-2020.ch01-data.x06-yoda-quotes
  (:require [clojure-csv.core :as csv]
            [clojure-north-2020.ch01-data.x02-functions :refer [csv-file->maps]]))

;; Not used today :( Sorry!
;; However, you can inspect it with our generic csv-file->maps function.
(comment
  (csv-file->maps "resources/yoda-corpus.csv"))