(ns clojure-north-2020.ch03-web.x03-responses
  (:require [clojure.pprint :as pp]
            [ring.adapter.jetty :as jetty]
            [ring.util.http-response :refer [content-type header not-found ok]]))

;; ## Use ring.util.http-response
;; In this namespace we introduce the ring.util.http-response library.
;; It is added to your project with the [metosin/ring-http-response "0.9.1"]
;; dependency.
;;
;; We can now formulate responses using simple functions instead of
;; hand-rolling maps for each endpoint.
;;
;; Note that this library does nothing more than modify maps.

;; With the exception of response formatting, everything here is identical to
;; the previous exercise.

;; Business Logic
(defn greet [greetee]
  (format "Hello, %s!" (or greetee "Clojurian")))

;; "Local" handlers
(defn hello-handler [{:keys [query-string] :as request}]
  (let [[_ greetee] (some->> query-string (re-matches #"name=(.+)"))]
    (ok (greet greetee))))

(defn request-dump-handler [request]
  (ok (with-out-str (pp/pprint request))))

;; "Global" handler which is mostly routing to local handlers
(defn handler [{:keys [uri] :as request}]
  (case uri
    "/hello" (hello-handler request)
    "/dump" (request-dump-handler request)
    (not-found "Sorry, I don't understand that path.")))

(defonce server (jetty/run-jetty #'handler {:host  "0.0.0.0"
                                            :port  3000
                                            :join? false}))

;; ## Exercise - Evaluate the following forms to see that these helper functions
;; are nothing more than simple data manipulators.
(comment
  ;Basic responses
  (ok "All I do is modify maps.")
  (not-found "You did't find me.")
  ;Use a threading macro with a basic response + response modifiers
  (-> (ok "Watch this")
      (content-type "text/plain")
      (header "Content-Disposition" "attachment; filename=\"foo.txt\""))

  (require '[clojure.java.browse :refer [browse-url]])
  (browse-url "http://localhost:3000")
  (.stop server)
  )
