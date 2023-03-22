(ns pug4clj.core-test
  (:require [clojure.test :refer :all]
            [pug4clj.core :refer :all]))

(deftest html-test
  (let [html (render)]
    (is (re-find #"available=yes" html))
    (is (re-find #"&lt;blink&gt;" html))
    (is (not (re-find #"available=no" html)))))
