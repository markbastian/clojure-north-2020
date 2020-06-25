(ns clojure-north-2020.ch04-application.x02-the-final-handler
  (:require [clojure-north-2020.ch01-data.x03-hero-data :as x03d]
            [clojure-north-2020.ch01-data.x04-hero-powers-data :as x04d]
            [clojure-north-2020.ch01-data.x05-supplemental-hero-data :as x05d]
            [clojure-north-2020.ch02-datalog.x03-hero-schema :as x03]
            [clojure-north-2020.ch02-datalog.x04-hero-powers-schema :as x04]
            [clojure-north-2020.ch02-datalog.x05-supplemental-hero-data-schema :as x05]
            [clojure-north-2020.ch02-datalog.x07-queries :as x07]
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
            [ring.util.http-response :refer [bad-request not-found ok]]
            [clojure.edn :as edn]))

;; ## Threading State: A Purely Functional API
;; We are now going to modify our API in a very simple way - We remove the
;; global reference to the datahike connection and instead add it as a function
;; parameter in our request. This may seem odd, but recall that Clojure handlers
;; are nothing more than a function that takes a request and returns a response.
;; All we need to do is inject our connection into the request to thread it
;; through the handler. We'll do this in the next section.
;;
;; This approach has several advantages:
;;
;; * To the extent that the parameters are values, the function is pure
;; * There is no global state floating around
;; * It is trivial to synthesize inputs to the handler
;; * It is trivial to consistently manage arguments for different environments
;;   (e.g. test, dev, staging, etc.)

;; Business Logic
(defn greet [greetee]
  (format "Hello, %s!" (or greetee "Clojurian")))

(defn load-schemas [conn]
  (d/transact conn x03/schema)
  (d/transact conn x04/schema)
  (d/transact conn x05/schema))

(defn load-data [conn]
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

(defn load-schemas-handler [{:keys [dh-conn] :as _request}]
  (load-schemas dh-conn)
  (ok "Schemas Loaded"))

(defn load-data-handler [{:keys [dh-conn] :as _request}]
  (ok (load-data dh-conn)))

(defn hero-data-handler [{:keys [params dh-conn] :as _request}]
  (let [n (params "name")]
    (try
      (ok (d/pull @dh-conn '[*] [:name n]))
      (catch Throwable e
        (not-found (format "Superhero \"%s\" not found." n))))))

(defn datom-count-handler [{:keys [dh-conn] :as _request}]
  (ok {:datoms (count @dh-conn)}))

(defn schema-handler [{:keys [dh-conn] :as _request}]
  (ok (map
        #(dissoc % :db/id)
        (d/q x07/schema-query @dh-conn))))

(defn hero-names-handler [{:keys [dh-conn] :as _request}]
  (ok (sort (d/q x07/name-query @dh-conn))))

(defn add-hero-handler [{:keys [body-params dh-conn] :as _request}]
  (try
    (let [{:keys [tempids]} (d/transact dh-conn [body-params])]
      (ok tempids))
    (catch Exception e
      (bad-request (.getMessage e)))))

(defn q-handler [{:keys [body-params dh-conn] :as _request}]
  (try
    (let [q (edn/read-string (:query body-params))]
      (ok (d/q q @dh-conn)))
    (catch Exception e
      (bad-request (.getMessage e)))))

;; "Global" handler which is mostly routing to local handlers.
;; Note that this DID NOT CHANGE AT ALL from the previous example.
;; The state is at the edges, just being piped through.
;; You could even put the hander/router in one ns, the function defs in
;; another, and use an alias or import to swap out the handlers.
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