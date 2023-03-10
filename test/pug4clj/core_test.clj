(ns pug4clj.core-test
  (:require [clojure.test :refer :all]
            [pug4clj.core :refer :all])
  (:import (de.neuland.pug4j Pug4J)))

(deftest a-test
  (is (re-find #"<title>Example Domain</title>" (Pug4J/render "./index.pug" {}))))
