(ns pug.core-test
  (:require [clojure.test :refer :all]
            [pug.core :refer :all]))

(deftest html-test
  (is (re-find #"available=yes" (render (config))))
  (is (re-find #"&lt;blink&gt;" (render (config))))
  (is (not (re-find #"available=no" (render (config))))))
