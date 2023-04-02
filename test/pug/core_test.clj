(ns pug.core-test
  (:require [clojure.test :refer :all]
            [pug.core :refer :all]))

(deftest html-test
  (let [actual (render
                 (config)
                 "test.pug"
                 {:pageName "list of <blink>books</blink>",
                  :books
                    [{:available true, :name "available=yes", :price 1}
                     {:available false, :name "available=no", :price "0"}]})]
    (is (re-find #"available=yes" actual))
    (is (re-find #"&lt;blink&gt;" actual))
    (is (not (re-find #"available=no" actual)))))

(deftest complex-test
  (testing "layouts and includes work"
    (let [actual (render (config) "complex/index.pug" {})]
      (is (re-find #"Welcome to My Website" actual))
      (is (re-find #"Sidebar" actual)))))