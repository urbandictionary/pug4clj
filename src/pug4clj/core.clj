(ns pug4clj.core
  (:require [clojure.java.io :as io]
            [clojure.walk :refer [stringify-keys]])
  (:import (de.neuland.pug4j PugConfiguration)
           (de.neuland.pug4j.template TemplateLoader)))

(def resource-template-loader
  (reify
    TemplateLoader
    (getLastModified [_ name]
      (.getLastModified (.openConnection (io/resource name))))
    (getReader [_ name] (io/reader (io/resource name)))))

(defn render
  []
  (let [config (doto (PugConfiguration.)
                 (.setTemplateLoader resource-template-loader))]
    (.renderTemplate
     config
     (.getTemplate config "index.pug")
     (stringify-keys
      {:pageName "list of <blink>books</blink>",
       :books [{:available true, :name "available=yes", :price 1}
               {:available false, :name "available=no", :price "0"}]}))))

(defn -main
  [& args]
  (println (render)))
