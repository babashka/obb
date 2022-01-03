(ns io.zane.obb.sci
  (:require [clojure.string :as string]
            [goog.object :as gobject]
            [sci.impl.interop :as interop]))

(defn safe-apply
  [method obj args]
  (if (js/Reflect.has method "apply")
    (.apply method obj (into-array args))
    (js/Function.prototype.apply.call method obj (into-array args))))

;; sci.impl.interop/invoke-instance-method
(defn invoke-instance-method
  [obj _target-class method-name args]
  (if-let [method (gobject/get obj method-name)]
    (if-let [call (gobject/get method "callAsFunction")]
      (let [f (.bind call method)]
        (.apply f obj (into-array args)))
      (safe-apply method obj args))
    (throw (js/Error. (str "Could not find instance method: " method-name)))))

(defn invoke-static-method
  [[class method-name] args]
  (if-let [method (gobject/get class method-name)]
    (safe-apply method class args)
    (let [method-name (str method-name)
          field (interop/get-static-field [class method-name])]
      (cond
        (not field)
        (throw (js/Error. (str "Could not find static method " method-name)))
        (string/ends-with? method-name ".")
        (interop/invoke-js-constructor field args)
        :else
        ;; why is this here??
        (apply field args)))))
