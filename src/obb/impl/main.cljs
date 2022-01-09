(ns obb.impl.main
  (:refer-clojure :exclude [prn slurp])
  (:require [clojure.core :as clojure]
            [clojure.tools.cli :as cli]
            [obb.impl.sci :as impl.sci]
            [sci.core :as sci]
            [sci.impl.interop :as interop]))

(def cli-options
  [["-e" "--eval <expr>"]])

(def app
  (delay (let [app (js/Application.currentApplication)]
           (set! (.-includeStandardAdditions app) true)
           app)))

(defn slurp
  "Like `clojure.core/slurp` but only accepts paths."
  [x]
  (.read @app (js/Path x #js {})))

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

(def ctx (sci/init {:classes {'js goog/global
                              :allow :all}}))

(defn eval-string
  "Evaluates a string using `ctx` as the context."
  [s]
  (sci/eval-string* ctx s))

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

(defn main [argv]
  (let [args (js->clj argv)
        {:keys [arguments summary] {form :eval} :options} (cli/parse-opts args cli-options)]
    (cond (some? form)
          (prn (eval-string form))

          (and (seq arguments)
               (= 1 (count arguments)))
          (let [form (slurp (first arguments))]
            (eval-string form))

          :else
          (println summary))
    js/undefined)) ; suppress printing of return value
