(ns clojure-north-2020.ch03-web.x04-reitit
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
            [ring.util.http-response :refer [content-type header not-found ok]]))

;; ## Reitit: A library for routing + Swagger
;; The details behind this ns are beyond the scope of this workshop, but the
;; key detail to remember is that one of the concerns of a handler is routing.
;; Until now we've just used a case statement on a request to do routing. This
;; ns expands on that idea with the following additions:
;;
;; 1. We use the reitit library to create data-driven routes. You should, by
;;    inspection, be able to determine how to add new routes.
;; 2. Reitit also adds several "middlewares" to the request that transforms the
;;    handler to do additional logic such as parameter extraction.
;; 3. We add a swagger ui handler to create a convenient Swagger UI.

;; Business Logic
(defn greet [greetee]
  (format "Hello, %s!" (or greetee "Clojurian")))

;; "Local" handlers
(defn hello-handler [{:keys [params] :as request}]
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