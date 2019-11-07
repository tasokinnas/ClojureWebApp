(ns clojure-web-project.routes.graphs
  (:require [incanter.core :as i]
            [incanter.charts :as c]
            [incanter.io :as iio]
            incanter.datasets))

(use '(incanter core datasets))
(def cars (incanter.datasets/get-dataset :cars))
(def iris (incanter.datasets/get-dataset :iris))
(def survey (incanter.datasets/get-dataset :survey))
(def arrests (incanter.datasets/get-dataset :us-arrests))
(def flow (incanter.datasets/get-dataset :flow-meter))

(def dataname
  {"cars"    cars
   "iris"    iris
   "survey"  survey
   "arrests" arrests
   "flow"    flow})

(defn graph-box [dataset x ]
  (c/box-plot
    (i/sel (dataname dataset) :cols (read-string x))
    :title (str "Box Plot of " x )
    :x-label (str x)))

(defn graph-line [dataset x y]
   (c/line-chart
     (i/sel (dataname dataset) :cols (read-string x))
     (i/sel (dataname dataset) :cols (read-string y))
     :title (str "Scatter Plot of " x " vs " y)
     :x-label (str x)
     :y-label (str y)))

(defn graph-scatter
  ([dataset x y]
   (c/scatter-plot
     (i/sel (dataname dataset) :cols (read-string x))
     (i/sel (dataname dataset) :cols (read-string y))
     :title (str "Scatter Plot of " x " vs " y)
     :x-label (str x)
     :y-label (str y))
   (println "called without group-by!"))
  ([dataset x y groupby]
   (c/scatter-plot
     (i/sel (dataname dataset) :cols (read-string x))
     (i/sel (dataname dataset) :cols (read-string y))
     :group-by (i/sel (dataname dataset) :cols (read-string groupby))
     :title (str "Scatter Plot of " x " vs " y)
     :x-label (str x)
     :y-label (str y))))

(defn graph-bar [dataset x y]
  (c/bar-chart (i/sel (dataname dataset) :cols (read-string x))
               (i/sel (dataname dataset) :cols (read-string y))
               :title (str "Bar Chart of " x " vs " y)
               :x-label (str x)
               :y-label (str y)))

(defn graph-histogram [dataset x]
  (c/histogram
    (i/sel (dataname dataset) :cols (read-string x))
               :title (str "Histogram of " x)
               :x-label (str x)
               :nbins 20))

(defn parse-graph-type [dataset graph x y groupby]
  (cond
    (= "scatter" graph) (graph-scatter dataset x y groupby)
    (= "bar" graph) (graph-bar dataset x y)
    (= "histogram" graph) (graph-histogram dataset x)
    (= "line" graph) (graph-line dataset x y )
    (= "box" graph) (graph-box dataset x)
    :else (str "You're broken on the inside")))

(defn graph-image [dataset graph x y groupby]
  (println "Graphing: " (str graph))
  (i/save (parse-graph-type dataset graph x y groupby) "resources/public/img/graph.png"))


