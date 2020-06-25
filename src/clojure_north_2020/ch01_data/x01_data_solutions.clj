(ns clojure-north-2020.ch01-data.x01-data-solutions)

(def data
  [{:name      "Batman"
    :alias     "Bruce Wayne"
    :powers    #{"Rich"}
    :weapons   #{"Utility Belt" "Kryptonite Spear"}
    :alignment "Chaotic Good"
    :nemesis   [{:name "Joker"}
                {:name "Penguin"}]}
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
   {:name      "Joker"
    :alias     "Jack Napier"
    :alignment "Chaotic Evil"
    :nemesis   [{:name "Batman"}]
    }
   ])