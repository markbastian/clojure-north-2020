(ns clojure-north-2020.ch03-web.x05-state
  (:require [clojure-north-2020.ch01-data.x03-hero-data :as x03d]
            [clojure-north-2020.ch01-data.x04-hero-powers-data :as x04d]
            [clojure-north-2020.ch01-data.x05-supplemental-hero-data :as x05d]
            [clojure-north-2020.ch02-datalog.datahike-utils :as du]
            [clojure-north-2020.ch02-datalog.x03-hero-schema :as x03]
            [clojure-north-2020.ch02-datalog.x04-hero-powers-schema :as x04]
            [clojure-north-2020.ch02-datalog.x05-supplemental-hero-data-schema :as x05]
            [clojure-north-2020.ch02-datalog.x07-queries :as x07]
            [clojure.edn :as edn]
            [clojure.pprint :as pp]
            [datahike.api :as d]
            [muuntaja.core :as m]
            [reitit.coercion.spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [ring.adapter.jetty :as jetty]
            [ring.util.http-response :refer [bad-request not-found ok]]))

;; ## State: Let's make our app do something
;; Any useful app will have some sort of backend state, usually a database. At
;; this point we are finally going to pull in our really cool datahike db and
;; add some cool queries and other interactions.

;; Here we create a global link to our ultimate db. This is actually terrible in
;; practice, but is often done by inexperienced Clojurians as they aren't really
;; sure how to handle stateful interactions as they try to develop functional
;; apps. What we want to do is "push our state to the edges" as is often said by
;; more advanced Clojurians. We'll discuss this concept in the next chapter and
;; implement the concept.
;;
;; Despite the ugliness of how we're wiring in our db, it is a beautiful thing
;; that we can arrive at this state from a completely bottom-up, data-driven
;; design. We modeled our system as data, built a schema around it, created a
;; few normalization functions as needed, and loaded our data into a database.
;; Queries were data driven and reusable. At each stage of this process we built
;; something useful and did not need to go back and modify our designs to wire
;; in future stages of our system. With the exception of the db, everything
;; until now has been purely functional. The db itself is a value when the
;; current state is retrieved for use in queries.
(defonce conn (du/conn-from-dirname "tmp/the-ultimate-db"))

;; Business Logic
(defn greet [greetee]
  (format "Hello, %s!" (or greetee "Clojurian")))

(defn load-schemas []
  (d/transact conn x03/schema)
  (d/transact conn x04/schema)
  (d/transact conn x05/schema))

(defn load-data []
  (let [before (count @conn)
        _ (d/transact conn (mapv x03/hero->dh-format (x03d/heroes-data)))
        _ (d/transact conn (vec (x04d/powers-data)))
        _ (d/transact conn (mapv x05/hero->dh-format (x05d/supplemental-hero-data)))
        after (count @conn)]
    {:datoms-before before
     :datoms-after  after
     :datoms-added  (- after before)}))

;; "Local" handlers
(defn hello-handler [{:keys [params] :as _request}]
  (ok (greet (params "name"))))

(defn request-dump-handler [request]
  (ok (with-out-str (pp/pprint request))))

(defn load-schemas-handler [_request]
  (load-schemas)
  (ok "Schemas Loaded"))

(defn load-data-handler [_request]
  (ok (load-data)))

(defn hero-data-handler [{:keys [params] :as _request}]
  (let [n (params "name")]
    (try
      (ok (d/pull @conn '[*] [:name n]))
      (catch Throwable e
        (not-found (format "Superhero \"%s\" not found." n))))))

(defn datom-count-handler [_request]
  (ok {:datoms (count @conn)}))

(defn schema-handler [_request]
  (ok (map
        #(dissoc % :db/id)
        (d/q x07/schema-query @conn))))

(defn hero-names-handler [_request]
  (ok (sort (d/q x07/name-query @conn))))

(defn add-hero-handler [{:keys [body-params] :as _request}]
  (try
    (let [{:keys [tempids]} (d/transact conn [body-params])]
      (ok tempids))
    (catch Exception e
      (bad-request (.getMessage e)))))

;; "Global" handler which is mostly routing to local handlers
(def router
  (ring/router
    [["/swagger.json"
      {:get {:no-doc  true
             :swagger {:info     {:title "my-api"}
                       :basePath "/"}
             :handler (swagger/create-swagger-handler)}}]
     ["/query"
      {:swagger {:tags ["Query Routes"]}}

      ["/load-schemas"
       {:get {:summary   "Load the schemas in the db."
              :responses {200 {:body string?}}
              :handler   load-schemas-handler}}]
      ["/load-data"
       {:get {:summary   "Load the data in the db."
              :responses {200 {:body {:datoms-before int?
                                      :datoms-after  int?
                                      :datoms-added  int?}}}
              :handler   load-data-handler}}]
      ["/hero"
       {:get {:summary    "Get data about a hero."
              :parameters {:query {:name string?}}
              :responses  {200 {:body {}}
                           404 {:body string?}}
              :handler    hero-data-handler}}]
      ["/datom-count"
       {:get {:summary   "Get the number of datoms in the system."
              :responses {200 {:body {:datoms int?}}}
              :handler   datom-count-handler}}]
      ["/schema"
       {:get {:summary   "Get the schema from the db."
              :responses {200 {:body [{}]}}
              :handler   schema-handler}}]
      ["/names"
       {:get {:summary   "Get all superhero names"
              :responses {200 {:body [string?]}}
              :handler   hero-names-handler}}]
      ["/add"
       {:post {:summary    "Add a new superhero"
               :responses  {200 {:body {}}}
               :parameters {:body {:name string?}}
               :handler    add-hero-handler}}]
      ;; ## Exercise: Add an endpoint
      ]
     ["/basic"
      {:swagger {:tags ["Basic Routes"]}}

      ["/hello"
       {:get {:summary    "Say hello"
              :parameters {:query {:name string?}}
              :responses  {200 {:body string?}}
              :handler    hello-handler}}]
      ["/dump"
       {:get {:summary   "Dump the request"
              :responses {200 {:body string?}}
              :handler   request-dump-handler}}]]]

    {:data {:coercion   reitit.coercion.spec/coercion
            :muuntaja   m/instance
            :middleware [parameters/parameters-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         exception/exception-middleware
                         muuntaja/format-request-middleware
                         coercion/coerce-response-middleware
                         coercion/coerce-request-middleware
                         multipart/multipart-middleware]}}))

(def handler
  (ring/ring-handler
    router
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/"})
      (ring/create-default-handler))))

(comment
  (defonce server (jetty/run-jetty #'handler {:host  "0.0.0.0"
                                              :port  3000
                                              :join? false}))

  (require '[clojure.java.browse :refer [browse-url]])
  (browse-url "http://localhost:3000")
  (.stop server)
  (du/cleanup conn)
  )
