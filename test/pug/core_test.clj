(ns pug.core-test
  (:require [clojure.test :refer :all]
            [pug.core :refer :all]))

(deftest html-test
  (is (re-find #"available=yes" (render)))
  (is (re-find #"&lt;blink&gt;" (render)))
  (is (not (re-find #"available=no" (render)))))
