(ns pug.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [pug.core :refer [render config pug-data]]
            [clojure.java.io :as io])
  (:import (java.io File)))

(deftest html-test
  (let [actual
          (render
            (config)
            "test.pug"
            {:page_name "list of <blink>books</blink>",
             :books
               [{:available true, :name-of-book "available=yes", :price 1}
                {:available false, :name-of-book "available=no", :price "0"}]})]
    (is (re-find #"available=yes" actual))
    (is (re-find #"&lt;blink&gt;" actual))
    (is (not (re-find #"available=no" actual)))))

(deftest complex-test
  (testing "layouts and includes work"
    (let [actual (render (config) "complex/index.pug" {})]
      (is (re-find #"Welcome to My Website" actual))
      (is (re-find #"Sidebar" actual))
      (is (re-find #"http://www.w3.org/2000/svg" actual)))))

(deftest io-test
  (testing "io/resource returns nil when a resource isn't found"
    (is (nil? (io/resource "asdf")))))

(defn render-pug
  [string]
  (let [file (File/createTempFile "temp" ".pug" (io/file "resources/tmp"))]
    (spit file string)
    (try (render
           (config)
           (str "tmp/" (.getName file))
           {:value "MyValue", :kw :My-Keyword, :deep-map {:deep-value 10}})
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
           (render-pug "p(id=`x-${value}`) asdf"))))
  (testing "keywords as input" (is (= "My-Keyword" (render-pug "= kw"))))
  (testing "deep map with underscores"
    (is (= "10" (render-pug "= deep_map.deep_value")))
    (testing "hello world"
      (is (re-find #"deep_map=" (render-pug "= locals"))))))

(deftest loop-test
  (is (= "123" (render-pug "for i in [1, 2, 3]\n  = i")))
  (is (= "<p x=\"1\"></p><p x=\"2\"></p><p x=\"3\"></p>"
         (render-pug "for i in [1, 2, 3]\n  p(x=i)"))))

(deftest test-pug-data
  (testing "simple" (is (= {"x" 5} (pug-data {:x 5}))))
  (testing "simple" (is (= {"x" "asdf"} (pug-data {:x :asdf}))))
  (testing "namespaced keyword"
    (is (= {"my_ns__a_b_c" "your-ns/d-e-f"}
           (pug-data {:my-ns/a-b-c :your-ns/d-e-f}))))
  (testing "recursive"
    (is (= {"a_b" {"c_d" {"e_f" 42}, "z" "hello"},
            "x_y" {"p_q" {"r_s" "world"}}}
           (pug-data {:a-b {:c-d {:e-f 42}, :z "hello"},
                      "x-y" {:p-q {:r-s "world"}}})))))