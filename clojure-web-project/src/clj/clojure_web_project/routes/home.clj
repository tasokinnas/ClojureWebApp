(ns clojure-web-project.routes.home
  (:require [clojure-web-project.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [clojure.java.io :as io]
            [hiccup.core :refer :all]
            [incanter.io :as iio]
            [ring.middleware.params :only [wrap-params]]
            [noir.io :as nio]
            [noir.response :as nresponse]
            [clojure-web-project.routes.outputI :as ipg]
            [clojure-web-project.routes.outputCSV :as csvpg]
            [clojure-web-project.routes.graphs :as graph]
            [clojure-web-project.routes.graphsCSV :as gcsv]))

(def cars (incanter.datasets/get-dataset :cars))
(def iris (incanter.datasets/get-dataset :iris))
(def survey (incanter.datasets/get-dataset :survey))
(def arrests (incanter.datasets/get-dataset :us-arrests))
(def flow (incanter.datasets/get-dataset :flow-meter))

(def resource-path "/tmp/")

(defn home-page []
  (layout/render "home.html"
                 {:files (->> (io/file "resources/data")
                              file-seq
                              (remove #(.isDirectory %)))}))

(defn about-page []
  (layout/render "about.html" {:readme (-> "docs/README.md" io/resource slurp)}))

(defn docs-page []
  (layout/render "docs.html"
                 {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn output-page []
  (layout/render "output.html"))

(defn iris-output-page []
  (layout/render "output_iris.html"
                 {:dataset "iris" :colnames (ipg/get-header-names iris)}))

(defn cars-output-page []
  (layout/render "output_cars.html"
                 {:dataset "cars" :colnames (ipg/get-header-names cars)}))

(defn survey-output-page []
  (layout/render "output_survey.html"
                 {:dataset "survey" :colnames (ipg/get-header-names survey)}))

(defn arrests-output-page []
  (layout/render "output_arrests.html"
                 {:dataset "arrests" :colnames (ipg/get-header-names arrests)}))

(defn flow-output-page []
  (layout/render "output_flow.html"
                 {:dataset "flow" :colnames (ipg/get-header-names flow)}))

(defn localfile-output-page []
  (layout/render "output_localf.html")
  )

(defn csv-graph-page []
  (layout/render "csvgraph.html"))

(defn graph-page []
  (layout/render "graph.html"))


(defn select-dataset [id id_str]
  (ipg/output-I id id_str)
  (redirect (str "/output_" id_str)))

(defn select-local-dataset [id filepath]
  (csvpg/output-CSV id filepath)
  (redirect "/output_localf"))


(defn render-graph-csv [filepath graph idx idy groupby]

  ;TODO Uncomment when done debugging for cleaner presentation
  ;;;;;;;;;;;;
  (try (gcsv/csv-graph-image filepath graph idx idy groupby)
         (redirect "/csvgraphpage")
       (catch Exception e (str "caught exception: " (.getMessage e)
                               "--> Pro-tip: You need to use columns with numerical values for most X and Y axes"))
       ))
;;;;;;;;;;;;

;TODO Uncomment for debugging
;;;;;;;;;;;
;(gcsv/csv-graph-image filepath graph idx idy groupby)
;(redirect "/csvgraphpage"))
;;;;;;;;;;;

(defn render-graph [ds graph idx idy groupby]
  ;TODO Uncomment when done debugging for cleaner presentation
  ;;;;;;;;;;;;
  (try (graph/graph-image ds graph idx idy groupby)
         (redirect "/graphpage")
       (catch Exception e (str "caught exception: " (.getMessage e)
                               "--> Pro-tip: You need to use columns with numerical values for most X and Y axes"))
       ))
;;;;;;;;;;;;

;TODO Uncomment for debugging
;;;;;;;;;;;;
;(graph/graph-image ds graph idx idy groupby)
;  (redirect "/graphpage"))
;;;;;;;;;;;;

(defroutes home-routes
           (GET "/" [] (home-page))
           (GET "/about" [] (about-page))
           (GET "/docs" [] (docs-page))
           (GET "/output" [dataset graph] (output-page))
           (GET "/output_localf" [] (localfile-output-page))
           (GET "/output_iris" [] (iris-output-page))
           (GET "/output_cars" [] (cars-output-page))
           (GET "/output_survey" [] (survey-output-page))
           (GET "/output_arrests" [] (arrests-output-page))
           (GET "/output_flow" [] (flow-output-page))
           (GET "/csvgraphpage" [] (csv-graph-page))
           (GET "/graphpage" [] (graph-page))
           (POST "/select-data" [id uploadf localf]
             (println id uploadf localf)
             (cond
               (= "iris" (str id)) (select-dataset iris "iris")
               (= "cars" (str id)) (select-dataset cars "cars")
               (= "survey" (str id)) (select-dataset survey "survey")
               (= "arrests" (str id)) (select-dataset arrests "arrests")
               (= "flow" (str id)) (select-dataset flow "flow")
               :else
               (select-local-dataset (iio/read-dataset localf :header true) localf)
               ;(str "You're broken " (str "uploadf:" uploadf ", localf: " localf ", incanter-id: " id))
               ))
           (POST "/graph" [dataset graph idx idy groupby]
             ;(println "Home:" dataset graph idx idy)
             (render-graph dataset graph idx idy groupby))

           (POST "/csvgraph" [filename graph idx idy groupby]
             (render-graph-csv filename graph idx idy groupby))

           ;;;;;;;;;;;;;;;;;;;;;;;;;
           ;TODO upload file functionality
           (POST "/upload" [fuploadf]
             (nio/upload-file resource-path fuploadf)
             (nresponse/redirect
               (str "/files/" (:filename fuploadf))))

           (GET "/files/:filename" [filename]
             (redirect (str resource-path filename)))
           ;;;;;;;;;;;;;;;;;;;;;;;;;;;

           )
