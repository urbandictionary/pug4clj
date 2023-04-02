(ns pug.core
  (:require [clojure.java.io :as io]
            [clojure.walk :refer [stringify-keys]])
  (:import (de.neuland.pug4j PugConfiguration)
           (de.neuland.pug4j.template TemplateLoader))
  (:gen-class))

(def resource-template-loader
  (reify
    TemplateLoader
      (getLastModified [_ name]
        (.getLastModified (.openConnection (io/resource name))))
      (getReader [_ name] (io/reader (io/resource name)))))

(defn config
  []
  (doto (PugConfiguration.) (.setTemplateLoader resource-template-loader)))

(defn render
  [config name model]
  (.renderTemplate config (.getTemplate config name) (stringify-keys model)))

(defn -main [& args] (println (render)))
