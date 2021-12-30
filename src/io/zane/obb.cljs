(ns io.zane.obb
  (:require [clojure.tools.cli :as cli]
            [sci.core :as sci]))

(def cli-options
  [["-e" "--eval <expr>"]])

(defn ^:export run [argv]
  (enable-console-print!)
  (sci/alter-var-root sci/print-fn (constantly *print-fn*))

  (let [args (js->clj argv)
        {:keys [summary] {form :eval} :options} (cli/parse-opts args cli-options)]
    (if-not (some? form)
      (println summary)
      (prn (sci/eval-string form {:classes {'js goog/global :allow :all}})))))
