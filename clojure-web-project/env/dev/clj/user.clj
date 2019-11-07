(ns user
  (:require [mount.core :as mount]
            clojure-web-project.core))

(defn start []
  (mount/start-without #'clojure-web-project.core/http-server
                       #'clojure-web-project.core/repl-server))

(defn stop []
  (mount/stop-except #'clojure-web-project.core/http-server
                     #'clojure-web-project.core/repl-server))

(defn restart []
  (stop)
  (start))


