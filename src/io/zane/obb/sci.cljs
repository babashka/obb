(ns io.zane.obb.sci
  (:require [goog.object :as gobject]))

;; sci.impl.interop/invoke-instance-method
(defn invoke-instance-method
  [obj _target-class method-name args]
  (if-let [method (gobject/get obj method-name)]
    (if-let [call (gobject/get method "callAsFunction")]
      (let [f (.bind call method)]
        (.apply f obj (into-array args)))
      (.apply method obj (into-array args)))
    (throw (js/Error. (str "Could not find instance method: " method-name)))))
