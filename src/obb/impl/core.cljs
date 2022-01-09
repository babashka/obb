(ns obb.impl.core
  (:refer-clojure :exclude [prn slurp])
  (:require [clojure.core :as clojure]
            [obb.impl.sci :as impl.sci]
            [sci.core :as sci]
            [sci.impl.interop :as interop]))

(def app
  (delay (let [app (js/Application.currentApplication)]
           (set! (.-includeStandardAdditions app) true)
           app)))

(set! (.-obb_lib_dir js/globalThis) "out")

(defn prefix []
  (.-obb_lib_dir js/globalThis))

(defn slurp
  "Like `clojure.core/slurp` but only accepts paths."
  [x]
  (.read @app (js/Path x #js {})))

(defn load-fn [{:keys [namespace]}]
  (case namespace
    obb.repl (let [code (slurp (str (prefix) "/obb_repl.js"))]
               ;; @zane: there might be a better way to evaluate in osascript JS
               (js/eval code)
               {:source ""})))

(defn display-string
  "Returns the JXA display string for an object specifier."
  [os]
  (js/Automation.getDisplayString os))

(defn object-specifier?
  "Returns true if x is an object specifier."
  [x]
  (js/ObjectSpecifier.hasInstance x))

(defn not-object-specifier-pred-1 [f]
  (fn [x]
    (when-not (object-specifier? x)
      (f x))))

(defn not-object-specifier-pred-2 [f]
  (fn [x y]
    (when-not (object-specifier? x)
      (f x y))))

(set! interop/invoke-instance-method impl.sci/invoke-instance-method)

(set! interop/invoke-static-method impl.sci/invoke-static-method)

(set! clojure/map? (not-object-specifier-pred-1 map?))

(set! clojure/meta (not-object-specifier-pred-1 meta))

(enable-console-print!)

(sci/alter-var-root sci/print-fn (constantly *print-fn*))

(def print*
  ;; All output from osascript by default goes to stderr. To get around this
  ;; we use the Objective-C bridge to write directly to stdout.

  ;; `delay` ensures that this import happens only when needed, and only once.
  (let [import (delay (.import js/ObjC "Foundation"))]
    (fn [s]
      @import
      (-> (.dataUsingEncoding (js/ObjC.wrap s) js/$.NSUTF8StringEncoding)
          (js/$.NSFileHandle.fileHandleWithStandardOutput.writeData)))))

(defn prn
  "Like `clojure.core/prn`, but will not crash if called on an object specifier.
  Object specifiers are printed as their display string with the prefix
  #org.babashka.obb/object-specifier."
  [x]
  (let [object-specifier-tag "#org.babashka.obb/object-specifier"]
    (print*
     (if (object-specifier? x)
       (str object-specifier-tag " " (pr-str (display-string x)))
       (pr-str x))))
  (print* \newline))

(def ctx (atom (sci/init {:load-fn load-fn
                          :classes {'js goog/global
                                    :allow :all}})))

(defn eval-string
  "Evaluates a string using `ctx` as the context."
  [s]
  (sci/eval-string* @ctx s))

(defn register-plugin! [_plug-in-name sci-opts]
  (swap! ctx sci/merge-opts sci-opts))
