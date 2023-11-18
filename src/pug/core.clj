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
  [kw delimiter]
  (if (keyword? kw)
    (if (namespace kw) (str (namespace kw) delimiter (name kw)) (name kw))
    kw))

(defn pug-data
  [input]
  (walk/postwalk (fn [form]
                   (if (map? form)
                     (->> form
                          (map (fn [[key value]] [(-> key
                                                      (kw->string "__"))
                                                  (kw->string value "/")]))
                          (into {}))
                     form))
                 input))

(defn config
  ([] (config {}))
  ([{:keys [shared-variables]}]
   (let [c (PugConfiguration.)]
     (.setTemplateLoader c resource-template-loader)
     (when shared-variables (.setSharedVariables c (pug-data shared-variables)))
     c)))

(defn render
  [config name model]
  (.renderTemplate config (.getTemplate config name) (pug-data model)))