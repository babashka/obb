(ns obb.impl.io
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defmacro inline-edn-value
  "Macro. Opens a reader on f and reads its contents as EDN as an associative
  data structure. Inlines the value for key k from that data structure."
  [f k]
  (-> (io/resource f)
      (slurp)
      (edn/read-string)
      (get k)))
