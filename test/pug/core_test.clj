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

(deftest io-test
  (testing "io/resource returns nil when a resource isn't found"
    (is (nil? (io/resource "asdf")))))

(defmacro with-tmp
  [file & body]
  `(let [~file (File/createTempFile "temp" ".pug" (io/file "resources/tmp"))]
     (try ~@body (finally (.delete ~file)))))

(defn render-pug
  ([string]
   (render-pug string
               {:value "MyValue", :kw :My-Keyword, :deep_map {:deep_value 10}}))
  ([string data]
   (with-tmp file
             (spit file string)
             (render (config) (str "tmp/" (.getName file)) data))))

(deftest defaults-test
  (with-tmp file
            (spit file "= a")
            (is (= "123"
                   (render (config {:shared-variables {:a 123}})
                           (str "tmp/" (.getName file))
                           {})))
            (is (= "456"
                   (render (config {:shared-variables {:a 123}})
                           (str "tmp/" (.getName file))
                           {:a 456})))))

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
         (render-pug "for i in [1, 2, 3]\n  p(x=i)")))
  (is (= "<p x=\"i is 1\"></p><p x=\"i is 2\"></p><p x=\"i is 3\"></p>"
         (render-pug "for i in [1, 2, 3]\n  p(x=`i is ${i}`)")))
  (is (= "0a1b2c" (render-pug "for x, i in ['a', 'b', 'c']\n  = i\n  = x"))))

(deftest helpers-test
  (is (= "123" (render-pug "= a" {:a 123})))
  (is (= "123" (render-pug "= a()" {:a (constantly 123)})))
  (is (= "123" (render-pug "= a.b()" {:a {:b (constantly 123)}}))))

(deftest test-pug-data
  (testing "simple" (is (= {"x" 5} (pug-data {:x 5}))))
  (testing "simple" (is (= {"x" "asdf"} (pug-data {:x :asdf}))))
  (testing "namespaced keyword"
    (is (= {"my-ns__a-b-c" "your-ns/d-e-f"}
           (pug-data {:my-ns/a-b-c :your-ns/d-e-f}))))
  (testing "recursive"
    (is (= {"a-b" {"c-d" {"e-f" 42}, "z" "hello"},
            "x-y" {"p-q" {"r-s" "world"}}}
           (pug-data {:a-b {:c-d {:e-f 42}, :z "hello"},
                      "x-y" {:p-q {:r-s "world"}}})))))

(deftest conditional-test
  (is (= "100" (render-pug "= x" {:x 100})))
  (is (= "true" (render-pug "= x" {:x true})))
  (is (= "" (render-pug "= x" {:x []})))
  (is (= "it is empty" (render-pug "if size(x) == 0\n  | it is empty" {:x []})))
  (is (= "it is empty" (render-pug "if empty(x)\n  | it is empty" {:x []})))
  (is (= "" (render-pug "if empty(x)\n  | it is empty" {:x [1]})))
  (is (= "asdf" (render-pug "- var x = \"asdf\"\n= x" {})))
  (is (= "less" (render-pug "- var x = 5\nif x < 10\n | less" {}))))