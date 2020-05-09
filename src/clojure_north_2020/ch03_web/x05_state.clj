(ns clojure-north-2020.ch03-web.x05-state
  (:require [clojure.pprint :as pp]
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
            [ring.util.http-response :refer [ok]]))

;; ## State: Let's make our app do something
;; Any useful app will have some sort of backend state, usually a database.

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
     ["/basic"
      {:swagger {:tags ["Basic Routes"]}}

      ["/hello"
       {:get {:summary    "Say hello"
              :parameters {:query {:name string?}}
              :responses  {200 {:body string?}}
              :handler    hello-handler}}]
      ["/dump"
       {:get {:summary    "Dump the request"
              :responses  {200 {:body string?}}
              :handler    request-dump-handler}}]]]

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

(defonce server (jetty/run-jetty #'handler {:host  "0.0.0.0"
                                            :port  3000
                                            :join? false}))

(comment
  (require '[clojure.java.browse :refer [browse-url]])
  (browse-url "http://localhost:3000")
  (.stop server)
  )
