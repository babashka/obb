(ns obb.impl.main
  (:require-macros [obb.impl.io :as io])
  (:require [clojure.string :as string]
            [obb.impl.core :as impl.core :refer [slurp ctx prefix]]))

(def help
  (let [version (io/inline-edn-value "project.edn" :version)]
    (str "obb v" version
         "\n\nUsage:
  obb <file>
  obb -e <expr>

Options:
  -e --eval  Evaluate an expression.")))

(defn parse-args
  [args]
  (loop [opts {}
         args args]
    (if-not (seq args)
      opts
      (let [farg (first args)
            nargs (next args)]
        (case farg
          ("-e" "--eval") (recur (assoc opts :expr (first nargs))
                                 (next nargs))
          (if-not (or (:expr args)
                      (:script args)
                      (string/starts-with? farg "-"))
            (assoc opts :script farg)
            (throw (ex-info (str "Unrecognized options:" args) {}))))))))

(defn main [argv]
  (let [{:keys [expr script]} (-> argv (js->clj) (parse-args))]
    (cond (some? expr)
          (impl.core/prn (impl.core/eval-string expr))

          (some? script)
          (let [form (impl.core/slurp script)]
            (impl.core/eval-string form))

          :else
          (let [src (slurp (str (prefix) "/obb_repl.js"))]
            (js/eval src)
            (let [env @(:env @ctx)
                  foo (get-in env [:namespaces 'obb.repl 'foo])]
              (foo) ;; TODO: launch REPL instead of printing summary
              (println help))))
    js/undefined)) ; suppress printing of return value
