(ns pug.core-test
  (:require [clojure.test :refer :all]
            [pug.core :refer :all]))

(deftest html-test
  (let [model {:pageName "list of <blink>books</blink>",
               :books [{:available true, :name "available=yes", :price 1}
                       {:available false, :name "available=no", :price "0"}]}]
    (is (re-find #"available=yes" (render (config) "test.pug" model)))
    (is (re-find #"&lt;blink&gt;" (render (config) "test.pug" model)))
    (is (not (re-find #"available=no" (render (config) "test.pug" model))))))

(deftest complex-test (render (config) "complex/index.pug" {}))