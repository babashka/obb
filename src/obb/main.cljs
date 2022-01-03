(ns obb.main
  (:refer-clojure :exclude [eval slurp])
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
  [x]
  (.read @app (js/Path x #js {})))

(defn object-specifier?
  "Returns true if x is an object specifier."
  [x]
  (js/ObjectSpecifier.hasInstance x))

(defn eval-string
  [s]
  (with-redefs [interop/invoke-instance-method impl.sci/invoke-instance-method
                interop/invoke-static-method impl.sci/invoke-static-method
                clojure/map? (every-pred (complement object-specifier?) map?)]
    (sci/eval-string s {:classes {'js goog/global
                                  :allow :all}})))

(defn -main [argv]
  (enable-console-print!)

  (sci/alter-var-root sci/print-fn (constantly *print-fn*))

  (let [args (js->clj argv)
        {:keys [arguments summary] {form :eval} :options} (cli/parse-opts args cli-options)]
    (cond (some? form)
          (eval-string form)

          (and (seq arguments)
               (= 1 (count arguments)))
          (let [form (slurp (first arguments))]
            (eval-string form))

          :else
          (println summary))))

(set! js/run -main)
