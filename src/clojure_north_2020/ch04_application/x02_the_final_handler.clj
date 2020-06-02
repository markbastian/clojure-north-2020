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
            [ring.util.http-response :refer [bad-request not-found ok]]))

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

;; "Local" handlers
(defn hello-handler [{:keys [params] :as _request}]
  (ok (greet (params "name"))))

(defn request-dump-handler [request]
  (ok (with-out-str (pp/pprint request))))


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
              :handler   (fn [{:keys [dh-conn] :as _request}]
                           (let [a (d/transact dh-conn x03/schema)
                                 b (d/transact dh-conn x04/schema)
                                 c (d/transact dh-conn x05/schema)])
                           (ok "Schemas Loaded"))}}]
      ["/load-data"
       {:get {:summary   "Load the data in the db."
              :responses {200 {:body {:datoms-before int?
                                      :datoms-after  int?
                                      :datoms-added  int?}}}
              :handler   (fn [{:keys [dh-conn] :as _request}]
                           (let [before (count @dh-conn)
                                 _ (count (d/transact dh-conn (mapv x03/hero->dh-format (x03d/heroes-data))))
                                 _ (count (d/transact dh-conn (vec (x04d/powers-data))))
                                 _ (count (d/transact dh-conn (mapv x05/hero->dh-format (x05d/supplemental-hero-data))))
                                 after (count @dh-conn)]
                             (ok {:datoms-before before
                                  :datoms-after  after
                                  :datoms-added  (- after before)})))}}]
      ["/hero"
       {:get {:summary    "Get data about a hero."
              :parameters {:query {:name string?}}
              :responses  {200 {:body {}}
                           404 {:body string?}}
              :handler    (fn [{:keys [params dh-conn] :as _request}]
                            (let [n (params "name")]
                              (try
                                (ok (d/pull @dh-conn '[*] [:name n]))
                                (catch Throwable e
                                  (not-found (format "Superhero \"%s\" not found." n))))))}}]
      ["/datom-count"
       {:get {:summary   "Get the number of datoms in the system."
              :responses {200 {:body {:datoms int?}}}
              :handler   (fn [{:keys [dh-conn] :as _request}]
                           (ok {:datoms (count @dh-conn)}))}}]
      ["/schema"
       {:get {:summary   "Get the schema from the db."
              :responses {200 {:body [{}]}}
              :handler   (fn [{:keys [dh-conn] :as _request}]
                           (ok (map
                                 #(dissoc % :db/id)
                                 (d/q x07/schema-query @dh-conn))))}}]
      ["/names"
       {:get {:summary   "Get all superhero names"
              :responses {200 {:body [string?]}}
              :handler   (fn [{:keys [dh-conn] :as _request}]
                           (ok (sort (d/q x07/name-query @dh-conn))))}}]
      ["/add"
       {:post {:summary    "Add a new superhero"
               :responses  {200 {:body {}}}
               :parameters {:body {:name string?}}
               :handler    (fn [{:keys [body-params dh-conn] :as _request}]
                             (try
                               (let [{:keys [tempids]} (d/transact dh-conn [body-params])]
                                 (ok tempids))
                               (catch Exception e
                                 (bad-request (.getMessage e)))))}}]]
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