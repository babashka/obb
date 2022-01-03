(ns obb.main
  (:require [obb.impl.main :as impl.main]))

(defn -main [argv]
  (impl.main/main argv))

(set! js/run -main)
