(ns clojure-getting-started.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [clojure-getting-started.facebook :as fb]
            ; Dependencies for Heroku Example
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello from Heroku with Clojure"})

(defroutes fb-routes
  (GET "/" [] (splash))
  (POST "/webhook" request (fb/route-request request) {:status 200})
  (GET "/webhook" request (fb/webhook-is-valid? request)))

(def app
  (-> (wrap-defaults fb-routes api-defaults)
      (wrap-keyword-params)
      (wrap-json-params)))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
