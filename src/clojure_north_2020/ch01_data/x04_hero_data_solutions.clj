(ns clojure-north-2020.ch01-data.x04-hero-data-solutions)

(comment
  (require '[clojure-north-2020.ch01-data.x03-hero-data :refer [heroes-data]])
  (take 10 (heroes-data))

  ;Compute the frequency of names if the name occurs > 1 time.
  (->> (heroes-data)
       (map :name)
       frequencies
       (filter (fn [[_ v]] (> v 1)))
       (into {}))

  ;Given a hero name, determine the duplicate values associated with
  ; non-distinct keys
  (->> (heroes-data)
       (filter (comp #{"Spider-Man"} :name))
       (apply merge-with (fn [a b] (if (= a b) a (flatten (vector a b)))))
       (filter (fn [[_ v]] (seq? v)))
       (into {}))

  (defn dupes [m]
    (->> m
         (apply merge-with (fn [a b] (if (= a b) [a] (vector a b))))
         (map (fn [[k v]] [k (distinct (flatten v))]))
         (filter (fn [[_ [_ s]]] s))
         (into {})))

  (->> (heroes-data)
       (filter (comp #{"Spider-Man"} :name))
       dupes)

  (dupes
    [{:name "Mark" :age 12}
     {:name "Mark" :age 12 :height 100}
     {:name "Mark" :age 14 :height 100}])
  )

