(ns integration-tests
  (:require [babashka.process :refer [process check]]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.test :as t :refer [deftest is]]))

(defmethod clojure.test/report :begin-test-var [m]
  (println "===" (-> m :var meta :name))
  (println))

(def cli-opts (atom nil))

(defn obb** [x & xs]
  (let [[opts args] (if (map? x)
                      [x xs]
                      [nil (cons x xs)])]
    (-> (process (into
                  (if (:dev @cli-opts)
                    ["osascript" "out/obb.js"]
                    ["out/bin/obb"]) args)
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

(deftest version-test
  (is (re-matches #"obb v[\d]+\.[\d]+\.[\d]+(\-SNAPSHOT)?\n"
                  (obb* "--version"))))

(defn parse-opts [opts]
  (let [[cmds opts] (split-with #(not (str/starts-with? % ":")) opts)]
    (into {:cmds cmds}
          (for [[arg-name arg-val] (partition 2 opts)]
            [(keyword (subs arg-name 1)) arg-val]))))

(defn run-tests [& args]
  (let [args (map str args)
        opts (parse-opts args)
        _ (reset! cli-opts opts)
        {:keys [error fail]}
        (t/run-tests 'integration-tests)]
    (when (pos? (+ error fail))
      (throw (ex-info "Tests failed" {:babashka/exit 1})))))
