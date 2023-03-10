(ns pug4clj.core-test
  (:require [clojure.test :refer :all]
            [pug4clj.core :refer :all]
            [clojure.java.io :as io]
            [clojure.walk :refer [stringify-keys]])
  (:import (de.neuland.pug4j Pug4J)))

(deftest a-test
  (let [actual (Pug4J/render
                 (io/resource "index.pug")
                 (stringify-keys
                   {:pageName "list of <blink>books</blink>",
                    :books
                      [{:available true, :name "available=yes", :price 1}
                       {:available false, :name "available=no", :price "0"}]}))]
    (is (re-find #"available=yes" actual))
    (is (re-find #"&lt;blink&gt;" actual))
    (is (not (re-find #"available=no" actual)))))
