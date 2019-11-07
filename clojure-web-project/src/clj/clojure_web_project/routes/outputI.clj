(ns clojure-web-project.routes.outputI
  (:require [clojure.data.csv :as csv]
            [net.cgrand.enlive-html :as enl]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [hiccup.core :refer :all]
            [clojure-web-project.routes.graphs :as gr]
            [incanter.core :as i]
            [incanter core charts datasets]))

(def cars (incanter.datasets/get-dataset :cars))
(def iris (incanter.datasets/get-dataset :iris))
(def survey (incanter.datasets/get-dataset :survey))
(def arrests (incanter.datasets/get-dataset :us-arrests))
(def flow (incanter.datasets/get-dataset :flow-meter))

(defn view-as-html [incanter-dataset]
  [:table {:border 1}
   [:thead
    [:tr
     (for [field-name (i/col-names incanter-dataset)]
       [:th field-name])]]
   [:tbody
    (for [r (second (second incanter-dataset))]
      [:tr (for [k (i/col-names incanter-dataset)]
             [:td (str (k r))])])]])

(defn get-header-names [incanter-dataset]
  (i/col-names incanter-dataset))

(enl/deftemplate add-table "templates/outputbase.html"
                 [table-html-data]
                 [:article#mainContent] (enl/content (enl/html table-html-data)))

(defn render
  [sd]
  (->> (map view-as-html [sd]) add-table (apply str)))

(defn dropdowns [incanter-dataset]
  (html [:div {:class "container"}
         [:div {:class "page-header"}
          [:div {:class "form-group"}
           [:form {:action "{{servlet-context}}/graph", :method "POST"} "{% csrf-field %}"
            [:div {:class "form-group"}
             [:label {:for "graph"} "Select Graph Type"]
             [:select {:class "form-control", :id "graph", :name "graph", :onchange "scattercheck(this); barcheck(this); histogramcheck(this);"}
              [:option {:value nil} "---- Select Type ----"]
              [:option {:value "histogram"} "histogram"]
              [:option {:value "scatter"} "scatter plot"]
              [:option {:value "bar"} "bar chart"]
              [:option {:value "line"} "line chart"]
              [:option {:value "box"} "box chart"]]
             [:br]
             [:div {:id "ifScatter", :style "display: none;"}
              [:label {:for "groupby"} "Select Group by Option "]
              [:select {:class "form-control", :id "groupby", :name "groupby"}
               [:option {:value "nogroups"} "---- Select Value ----"]
               (for
                 [field-name (get-header-names incanter-dataset)]
                 [:option {:value field-name} field-name])]]
             [:br]
             [:div {:id "ifX", :style "display: none;"}
              [:label {:for "idx"} "Select X-Axis"]
              [:select {:class "form-control", :id "idx", :name "idx"}
               [:option {:value nil} "---- Select Value ----"]
               (for [field-name (i/col-names incanter-dataset)]
                 [:option {:value field-name} field-name])]]
             [:div {:id "ifY", :style "display: none;"}
              [:label {:for "idy"} "Select Y-Axis"]
              [:select {:class "form-control", :id "idy", :name "idy"}
               [:option {:value nil} "---- Select Value ----"]
               (for [field-name (i/col-names incanter-dataset)]
                 [:option {:value field-name} field-name])]]
             [:input {:value "{{dataset}}", :name "dataset", :type "hidden"}]
             [:br]
             [:br]
             [:input {:type "submit"}]]]
           [:script "function scattercheck(that) {\n                                         if (that.value == \"scatter\" || that.value == \"line\") {\n                                            document.getElementById(\"ifX\").style.display = \"block\";\n                                            document.getElementById(\"ifY\").style.display = \"block\";\n                                            document.getElementById(\"ifScatter\").style.display = \"block\";\n\n                                        }\n                                    }\n"]
           [:script "function barcheck(that) {\n                                         if (that.value == \"bar\"  || that.value == \"line\") {\n                                            document.getElementById(\"ifX\").style.display = \"block\";\n                                            document.getElementById(\"ifY\").style.display = \"block\";\n                                            document.getElementById(\"ifScatter\").style.display = \"none\";\n\n                                        }\n                                    }\n"]
           [:script "function histogramcheck(that) {\n                                         if (that.value == \"histogram\" || that.value == \"box\") {\n                                            document.getElementById(\"ifX\").style.display = \"block\";\n                                            document.getElementById(\"ifY\").style.display = \"none\";\n                                            document.getElementById(\"ifScatter\").style.display = \"none\";\n\n                                        }\n                                    }\n"]]
          [:h1 "Data Output"]]]))

(defn output-local
  [ds]
  (spit "resources/templates/selectaxislocal.html" (dropdowns ds))
  (spit (str "resources/templates/output.html") (render ds))
  (println "Generated HTML"))

(defn output-I
  [ds ds_str]
  ;(println ds ds_str)
  (spit (str "resources/templates/output_" ds_str".html") (render ds))
  (println "Generated HTML"))