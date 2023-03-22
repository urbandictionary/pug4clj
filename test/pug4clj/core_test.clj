(ns pug4clj.core-test
  (:require [clojure.test :refer :all]
            [pug4clj.core :refer :all]
            [clojure.java.io :as io]
            [clojure.walk :refer [stringify-keys]])
  (:import (de.neuland.pug4j PugConfiguration)
           (de.neuland.pug4j.template TemplateLoader)))

(def resource-template-loader
  (reify
    TemplateLoader
      (getLastModified [_ name]
        (.getLastModified (.openConnection (io/resource name))))
      (getReader [_ name] (io/reader (io/resource name)))))

(deftest html-test
  (let [config (doto (PugConfiguration.)
                 (.setTemplateLoader resource-template-loader))
        html (.renderTemplate
               config
               (.getTemplate config "index.pug")
               (stringify-keys
                 {:pageName "list of <blink>books</blink>",
                  :books
                    [{:available true, :name "available=yes", :price 1}
                     {:available false, :name "available=no", :price "0"}]}))]
    (is (re-find #"available=yes" html))
    (is (re-find #"&lt;blink&gt;" html))
    (is (not (re-find #"available=no" html)))))
