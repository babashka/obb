(ns integration-tests
  (:require [babashka.process :refer [process check]]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.test :as t :refer [deftest is]]))

(defmethod clojure.test/report :begin-test-var [m]
  (println "===" (-> m :var meta :name))
  (println))

(defn obb** [x & xs]
  (let [[opts args] (if (map? x)
                      [x xs]
                      [nil (cons x xs)])]
    (-> (process (into ["out/bin/obb"] args)
                 (merge {:out :string
                         :err :inherit}
                        opts)))))

(defn obb* [& xs]
  (-> (apply obb** xs)
      check
      :out))

(defn obb [& args]
  (let [res (apply obb* args)]
    (when (string? res)
      (edn/read-string res))))

(deftest expression-test
  (is (= 6 (obb "-e" "(+ 1 2 3)")))
  (is (= "Hello, world!" (obb "-e" "\"Hello, world!\""))))

(deftest object-specifier-var-ref-test
  (is (str/includes? (obb* "-e"
                           (pr-str '(do
                                      (def ui-server (-> (js/Application "System Events")
                                                         (.-applicationProcesses)
                                                         (.byName "SystemUIServer")))
                                      ui-server)))
                     "SystemUIServer")))

(deftest object-specifier-tagged-literal-test
  (is (str/starts-with? (obb* "-e" (pr-str '(js/Application "Safari")))
                        "#org.babashka.obb/object-specifier")))

(defn parse-opts [opts]
  (let [[cmds opts] (split-with #(not (str/starts-with? % ":")) opts)]
    (into {:cmds cmds}
          (for [[arg-name arg-val] (partition 2 opts)]
            [(keyword (subs arg-name 1)) arg-val]))))

(defn run-tests [& args]
  (let [opts (parse-opts args)
        {:keys [error fail]}
        (if (empty? (dissoc opts :cmds))
          (t/run-tests 'integration-tests)
          (when-let [o (:only opts)]
            (let [o (symbol o)]
              (if (qualified-symbol? o)
                (do
                  (println "Testing" o)
                  (binding [t/*report-counters* (atom t/*initial-report-counters*)]
                    (t/test-var (resolve o))
                    @t/*report-counters*))
                (t/run-tests o)))))]
    (when (pos? (+ error fail))
      (throw (ex-info "Tests failed" {:babashka/exit 1})))))
