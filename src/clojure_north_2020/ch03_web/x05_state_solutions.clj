(ns clojure-north-2020.ch03-web.x05-state-solutions
  (:require [clojure.edn :as edn]
            [datahike.api :as d]
            [ring.util.http-response :refer [bad-request ok]]))

(comment
  ["/q"
   {:post {:summary    "Invoke a generic query on the db."
           :parameters {:body {:query string?}}
           :handler    (fn [{:keys [body-params] :as _request}]
                         (try
                           (let [q (edn/read-string (:query body-params))]
                             (ok (d/q q @conn)))
                           (catch Exception e
                             (bad-request (.getMessage e)))))}}]

  ;Example request with content type of application/edn
  {:query "[:find [(pull ?e [*]) ...]
    :in $
    :where
    [?e :db/ident ?ident]]"}

  ;Use later for state threaded version
  ["/q"
   {:post {:summary    "Invoke a generic query on the db."
           :responses  {200 []}
           :parameters {:body {:query string?}}
           :handler    (fn [{:keys [body-params dh-conn] :as _request}]
                         (try
                           (let [q (edn/read-string (:query body-params))]
                             (ok (d/q q @dh-conn)))
                           (catch Exception e
                             (bad-request (.getMessage e)))))}}])