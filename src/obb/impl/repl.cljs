(ns obb.impl.repl
  (:require [obb.impl.core :as impl.core]
            [sci.core :as sci]))

(defn foo []
  ;; TODO: launch REPL
  )

(impl.core/register-plugin!
 ::repl
 {:namespaces
  {'obb.repl {'foo (sci/copy-var foo
                                 (sci/create-ns 'obb.repl nil))}}})
