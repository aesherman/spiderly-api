(ns spiderly-api.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

; Simple Body Page
(defn simple-body-page [req]
  {:status  200
    :headers {"Content-Type" "text/html"}
    :body    "Welcome to Spiderly"})

; request-example
(defn request-example [req]
      {:status  200
      :headers {"Content-Type" "text/html"}
      :body    (->>
                (pp/pprint req)
                (str "Request Object: " req))})

(defn hello-name [req] ;(3)
{:status  200
  :headers {"Content-Type" "text/html"}
  :body    (->
            (pp/pprint req)
            (str "Hello " (:name (:params req))))})

; my spider-collection mutable collection vector
(def spider-collection (atom []))

;Collection Helper functions to add a new spider
(defn addspider [commonname latinname venomous]
  (swap! spider-collection conj {:commonname (str/capitalize commonname) :latinname (str/capitalize latinname) :venomous (boolean venomous) }))

; Example JSON objects
(addspider "American House Spider" "(Parasteatoda tepidariorum)" true)
(addspider "Ant Mimic Spider" "(Castianeira longipalpis)" true)
(addspider "Arabesque Orbweaver" "(Neoscona arabesca)" false)
(addspider "Arrowhead Orbweaver" "(Verrucosa arenata)" true)
(addspider "Arrow-shaped Micrathena Spider" "(Micrathena sagitatta)" true)

; Return List of Spiders
(defn spider-handler [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (str (json/write-str @spider-collection))})

(defroutes app-routes
  (GET "/" [] simple-body-page)
  (GET "/request" [] request-example)
  (GET "/hello" [] hello-name)
  (GET "/spiders" [] spider-handler)
  (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    ; Run the server without ring defaults
    ; (server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
