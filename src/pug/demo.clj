(ns pug.demo
  (:require [pug.core :as pug])
  (:gen-class))

(defn -main [& [name]] (println (pug/render (pug/config) name {:books []})))
