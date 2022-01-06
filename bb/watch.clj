(ns watch
  (:require [babashka.pods :as pods]
            [babashka.tasks :as tasks]
            [babashka.wait :as wait]
            [clojure.string :as str]))

(future (tasks/run 'shadow-server))

(wait/wait-for-path ".shadow-cljs/server.pid")

(def notification-expr
  (-> (slurp "examples/notification.cljs")
      (str/replace
       "All graphics have been converted."
       "Compilation successful.")
      (str/replace
       "My Graphic Processing Script"
       "obb")))

(defn notification []
  (tasks/shell "osascript out/obb.js" "-e" notification-expr))

(defn dev-compile []
  (tasks/run 'shadow-dev-compile)
  (notification))

(dev-compile)

(pods/load-pod 'org.babashka/filewatcher "0.0.1")

(require '[pod.babashka.filewatcher :as fw])

(fw/watch "src" (fn [ev]
                  (when-not (= :notice/write (:type ev))
                    (prn ev)
                    (dev-compile)))
          {:delay-ms 1000})

@(promise)
