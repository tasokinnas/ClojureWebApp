(ns clojure-web-project.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [clojure-web-project.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[clojure-web-project started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[clojure-web-project has shut down successfully]=-"))
   :middleware wrap-dev})
