(ns clojure-north-2020.ch03-web.x02-routes
  (:require [clojure.pprint :as pp]
            [ring.adapter.jetty :as jetty]))

;; ## Handler Roles/Concerns
;; Handlers have 3 main concerns:
;; * Routing - what subhandler/business logic is to be executed
;; * Business Logic - The actual logic you want to execute
;; * Response - Transform the BI result into an appropriate HTTP response

;In this namespace we split out our API, handler, and routing logic.

;; ### Protip - Separate logic from handlers
;; Business logic should know *nothing* about the calling context. If you are
;; returning http response codes or passing in web concepts you are complecting
;; your application. This particular "API" is contrived, but as we'l see in the
;; future we can use completely independent API logic in our servers without the
;; logic knowing anything about its surrounding context.
(defn greet [greetee]
  (format "Hello, %s!" (or greetee "Clojurian")))

;; ### Protip - Separate handlers from routing
;; A well written handler contains very little, if any business logic. It should
;; simply parse a request, invoke external business logic, and format a
;; response, including setting proper response codes for non-happy-path
;; execution.
(defn hello-handler [{:keys [query-string] :as _request}]
  (let [[_ greetee] (some->> query-string (re-matches #"name=(.+)"))]
    {:status 200
     :body   (greet greetee)}))

(defn request-dump-handler [request]
  {:status 200
   :body   (with-out-str (pp/pprint request))})

;; ## The Global Handler
;; Here we have some very simple routing logic based on case matching the uri
;; from the request. Aside from demonstrating the concept that handlers are
;; nothing more than a request->return map function you probably want to use
;; a real routing library in real life like
;; [Reitit](https://metosin.github.io/reitit/) or
;; [Compojure](https://github.com/weavejester/compojure).
;;
;; ### Protip - Only perform routing in the global handler
;; Defer handling of specific routes to their own functions. This facilitates
;; independent testing of each handler and recomposition of routes.
(defn handler [{:keys [uri] :as request}]
  (case uri
    "/hello" (hello-handler request)
    "/dump" (request-dump-handler request)
    {:status 404
     :body "Sorry, I only understand hello and dump"}))

(defonce server (jetty/run-jetty #'handler {:host  "0.0.0.0"
                                            :port  3000
                                            :join? false}))

(comment
  (require '[clojure.java.browse :refer [browse-url]])
  (browse-url "http://localhost:3000")
  (.stop server))