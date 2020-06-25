(ns clojure-north-2020.ch02-datalog.x04-hero-powers-schema-solutions)

(comment
  ;; ## Exercise: Who has the power :levitation?
  (d/q
    '[:find [?name ...]
      :in $ ?power
      :where
      [?e :name ?name]
      [?e :powers ?power]]
    @conn :levitation)

  ;; ## Exercise: Who has the same powers as the named super? Return a map of
  ;; power to sequence of names of hero with shared power.
  (let [hero-name "Yoda"]
    (->> (d/q
           '[:find ?that-name ?power
             :in $ ?name
             :where
             [?e :name ?name]
             [?e :powers ?power]
             [?f :powers ?power]
             [?f :name ?that-name]
             [(not= ?e ?f)]]
           @conn hero-name)
         (reduce (fn [m [n p]] (update m p conj n)) {})))

  ;; ## Exercise: List heroes by number of powers. Who has the most?
  (sort-by
    second
    (d/q
      '[:find ?name (count ?power)
        :in $
        :where
        [?e :name ?name]
        [?e :powers ?power]]
      @conn)))
