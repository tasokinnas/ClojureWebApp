(ns clojure-web-project.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[clojure-web-project started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[clojure-web-project has shut down successfully]=-"))
   :middleware identity})
