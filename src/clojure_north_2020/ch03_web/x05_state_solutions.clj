(ns clojure-north-2020.ch03-web.x05-state-solutions
  (:require [clojure.edn :as edn]
            [datahike.api :as d]
            [ring.util.http-response :refer [bad-request ok]]))

["/q"
 {:post {:summary    "Invoke a generic query on the db."
         :responses  {200 []}
         :parameters {:body {:query string?}}
         :handler    (fn [{:keys [body-params dh-conn] :as _request}]
                       (try
                         (let [q (edn/read-string (:query body-params))]
                           (ok (d/q q @dh-conn)))
                         (catch Exception e
                           (bad-request (.getMessage e)))))}}]