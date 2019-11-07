(ns clojure-web-project.routes.graphsCSV
  (:require [incanter.core :as i]
            [incanter.charts :as c]
            [incanter.io :as iio]
            incanter.datasets))


(defn dataname [filepath]
  (iio/read-dataset filepath :header true))



(defn graph-histogram [filepath x]
  (c/histogram
          (read-string x)
          :data (dataname filepath)
          :y-label "Count"
          :nbins 20)
 )

(defn graph-box [filepath x ]
  (c/box-plot
    (read-string x)
    :data (dataname filepath)
    :title (str "Box Plot of " x )
    :x-label (str x)))

(defn graph-line [filepath x y]
  (c/line-chart
    (read-string x)
    (read-string y)
    :data (dataname filepath)
    :title (str "Scatter Plot of " x " vs " y)
    :x-label (str x)
    :y-label (str y)))

(defn graph-scatter
  ([filepath x y]
   (c/scatter-plot
     (read-string x)
     (read-string y)
     :data (dataname filepath)
     :title (str "Scatter Plot of " x " vs " y)
     :x-label (str x)
     :y-label (str y))
   (println "called without group-by!"))
  ([filepath x y groupby]
   (c/scatter-plot
     (read-string x)
     (read-string y)
     :data (dataname filepath)
     :group-by (read-string groupby)
     :title (str "Scatter Plot of " x " vs " y)
     :x-label (str x)
     :y-label (str y))))

(defn graph-bar [filepath x y]
  (c/bar-chart (read-string x)
               (read-string y)
               :data (dataname filepath)
               :title (str "Bar Chart of " x " vs " y)
               :x-label (str x)
               :y-label (str y)))


(defn parse-graph-type [filename graph x y groupby]
  (cond
    (= "scatter" graph) (graph-scatter filename x y groupby)
    (= "bar" graph) (graph-bar filename x y)
    (= "histogram" graph) (graph-histogram filename x)
    (= "line" graph) (graph-line filename x y )
    (= "box" graph) (graph-box filename x)
    :else (str "You're broken on the inside")))

(defn csv-graph-image [filename graph x y groupby]
  (println "Graphing: " (str graph))
  (i/save (parse-graph-type filename graph x y groupby) "resources/public/img/graph.png"))


