(ns pug.core-test
  (:require [clojure.test :refer :all]
            [pug.core :refer :all]))

(deftest html-test
  (let [html (render)]
    (is (re-find #"available=yes" html))
    (is (re-find #"&lt;blink&gt;" html))
    (is (not (re-find #"available=no" html)))))
