(ns pug.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [pug.core :refer [render config]]
            [clojure.java.io :as io])
  (:import (java.io File)))

(deftest html-test
  (let [actual
          (render
            (config)
            "test.pug"
            {:page_name "list of <blink>books</blink>",
             :books
               [{:available true, :name_of_book "available=yes", :price 1}
                {:available false, :name_of_book "available=no", :price "0"}]})]
    (is (re-find #"available=yes" actual))
    (is (re-find #"&lt;blink&gt;" actual))
    (is (not (re-find #"available=no" actual)))))

(deftest complex-test
  (testing "layouts and includes work"
    (let [actual (render (config) "complex/index.pug" {})]
      (is (re-find #"Welcome to My Website" actual))
      (is (re-find #"Sidebar" actual))
      (is (re-find #"http://www.w3.org/2000/svg" actual)))))

(deftest io-test (is (nil? (io/resource "asdf"))))

(defn render-pug
  [string]
  (let [file (File/createTempFile "temp" ".pug" (io/file "resources/tmp"))]
    (spit file string)
    (try (render (config) (str "tmp/" (.getName file)) {:value "MyValue"})
         (finally (.delete file)))))

(deftest render-test
  (testing "hello world" (is (= "<p>asdf</p>" (render-pug "p asdf"))))
  (testing "use variable" (is (= "<p>MyValue</p>" (render-pug "p= value"))))
  (testing "literal attribute"
    (is (= "<p class=\"foo\">asdf</p>" (render-pug "p(class='foo') asdf"))))
  (testing "expression in attribute"
    (is (= "<p id=\"yes\">asdf</p>"
           (render-pug "p(id=true ? 'yes' : 'no') asdf"))))
  (testing "variable as attribute"
    (is (= "<p id=\"MyValue\">asdf</p>" (render-pug "p(id=value) asdf"))))
  (testing "interpolation"
    (is (= "<p id=\"x-MyValue\">asdf</p>"
           (render-pug "p(id=`x-${value}`) asdf")))))