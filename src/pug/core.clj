(ns pug.core
  (:require [clojure.java.io :as io]
            [clojure.walk :as walk]
            [clojure.string :as str])
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

(defn kw->string
  [delimiter kw]
  (if (keyword? kw)
    (if (namespace kw) (str (namespace kw) delimiter (name kw)) (name kw))
    kw))

(defn pug-data
  [input]
  (->> input
       (walk/postwalk (fn [form]
                        (if (map? form)
                          (->> form
                               (map (fn [[key value]] [(str/replace
                                                         (kw->string "__" key)
                                                         #"-"
                                                         "_") value]))
                               (into {}))
                          form)))
       (walk/postwalk (partial kw->string "/"))))

(defn config
  []
  (doto (PugConfiguration.) (.setTemplateLoader resource-template-loader)))

(defn render
  [config name model]
  (.renderTemplate config (.getTemplate config name) (pug-data model)))