(ns obb.main
  (:require [obb.impl.main :as impl.main]))

(defn ^:export main [argv]
  (impl.main/main argv))

(set! js/globalThis.run main)
