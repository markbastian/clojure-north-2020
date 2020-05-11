(ns clojure-north-2020.ch03-web.x01-basic
  (:require [clojure.pprint :as pp]
            [ring.adapter.jetty :as jetty]))

;; ## Clojure Web App Basics
;; Now that we have a cool database, let's build a backend application that
;; allows us to interact with it via RESTful service. We'll start with a simple
;; explanation of what and how web apps work in Clojure. Once we understand how
;; web apps work we'll wire in our database. Finally, we'll look at a technique
;; for "pushing state to the edges" of our application for a truly functional
;; app that still has state.
;;
;; ### The Basic Web App
;; Here we have the most basic Clojure application possible, consisting of
;; exactly two things:
;;
;; 1. A jetty web server (Add [ring/ring-jetty-adapter "1.8.0"] to your
;; dependencies.)
;; 2. A handler - a single function that takes a request (a map) and returns a
;; response (also a map)

(defn hello-handler [_request]
  {:status 200
   :body   "Hello Clojure!"})

(defn request-dump-handler
  [request]
  {:status 200
   :body   (with-out-str (pp/pprint request))})

(def handler hello-handler)

;; Our one and only web server. Notice that the hander is "var quoted" using
;; `#'`. This means the handler will resolve to the handler function when
;; called rather than being evaluated to the value of the handler when the
;; server is created. This allows for live code reloading.
(defonce server (jetty/run-jetty #'handler {:host  "0.0.0.0"
                                            :port  3000
                                            :join? false}))

(comment
  (require '[clojure.java.browse :refer [browse-url]])
  (browse-url "http://localhost:3000")
  (.stop server))
