(ns clojure-north-2020.ch03-web.x05-state-solutions
  (:require [clojure.edn :as edn]
            [datahike.api :as d]
            [ring.util.http-response :refer [bad-request ok]]))

(comment
  (defn q-handler [{:keys [body-params] :as _request}]
    (try
      (let [q (edn/read-string (:query body-params))]
        (ok (d/q q @conn)))
      (catch Exception e
        (bad-request (.getMessage e)))))

  ["/q"
   {:post {:summary    "Invoke a generic query on the db."
           :parameters {:body {:query string?}}
           :handler    q-handler}}]

  ;Example request with content type of application/edn
  {:query "[:find [(pull ?e [*]) ...]
    :in $
    :where
    [?e :db/ident ?ident]]"})