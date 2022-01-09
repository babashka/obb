(ns obb.impl.main
  (:require [clojure.tools.cli :as cli]
            [obb.impl.core :as impl.core :refer [slurp ctx prefix]]))

(def cli-options
  [["-e" "--eval <expr>"]])

(defn main [argv]
  (let [args (js->clj argv)
        {:keys [arguments summary] {form :eval} :options} (cli/parse-opts args cli-options)]
    (cond (some? form)
          (impl.core/prn (impl.core/eval-string form))

          (and (seq arguments)
               (= 1 (count arguments)))
          (let [form (slurp (first arguments))]
            (impl.core/eval-string form))

          :else
          (let [src (slurp (str (prefix) "/obb_repl.js"))]
            (js/eval src)
            (let [env @(:env @ctx)
                  foo (get-in env [:namespaces 'obb.repl 'foo])]
              (foo) ;; TODO: launch REPL instead of printing summary
              (println summary))))
    js/undefined)) ; suppress printing of return value
