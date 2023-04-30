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
  (let [file (File/createTempFile "temp" ".pug" (io/file "resources/tmp"))
        resources-path
          (str (.getName (.getParentFile file)) "/" (.getName file))]
    (spit file string)
    (try (render (config) resources-path {:value "MyValue"}) (finally (.delete file)))))

(deftest render-test
  (is (= "<p>asdf</p>" (render-pug "p asdf")))
  (is (= "<p>MyValue</p>" (render-pug "p= value")))
  (is (= "<p class=\"foo\">asdf</p>" (render-pug "p(class='foo') asdf")))
  (is (= "<p class=\"foo\">asdf</p>" (render-pug "p(class='foo') asdf"))))