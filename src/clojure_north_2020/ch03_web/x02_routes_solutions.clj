(ns clojure-north-2020.ch03-web.x02-routes-solutions)

(defn greet [greetee]
  (format "Hello, %s!" (or greetee "Clojurian")))

(defn hello-handler [{:keys [query-string] :as _request}]
  (let [[_ greetee] (some->> query-string (re-matches #"name=(.+)"))]
    {:status 200
     :body   (greet greetee)}))