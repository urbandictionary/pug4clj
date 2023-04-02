(ns pug.core
  (:require [clojure.java.io :as io]
            [clojure.walk :refer [stringify-keys]])
  (:import (de.neuland.pug4j PugConfiguration)
           (de.neuland.pug4j.template TemplateLoader)))

(def resource-template-loader
  (reify
    TemplateLoader
      (getBase [_] "")
      (getExtension [_] "pug")
      (getLastModified [_ name]
        (let [resource (io/resource name)]
          (assert resource (str "Resource not found: " name))
          (.getLastModified (.openConnection resource))))
      (getReader [_ name]
        (let [resource (io/resource name)]
          (assert resource (str "Resource not found: " name))
          (io/reader resource)))))

(defn config
  []
  (doto (PugConfiguration.) (.setTemplateLoader resource-template-loader)))

(defn render
  [config name model]
  (.renderTemplate config (.getTemplate config name) (stringify-keys model)))