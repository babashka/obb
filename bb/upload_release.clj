(ns upload-release
  (:require [borkdude.gh-release-artifact :as ghr]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str]))

(defn current-branch []
  (or (System/getenv "APPVEYOR_PULL_REQUEST_HEAD_REPO_BRANCH")
      (System/getenv "APPVEYOR_REPO_BRANCH")
      (System/getenv "CIRCLE_BRANCH")
      (-> (sh "git" "rev-parse" "--abbrev-ref" "HEAD")
          :out
          str/trim)))

(defn release [& args]
  (let [ght (System/getenv "GITHUB_TOKEN")
        file (first args)
        version (second args)
        branch (current-branch)]
    (if (and ght (contains? #{"master" "main"} branch))
      (do (assert file "File name must be provided")
          (println "Uploading" file)
          (ghr/overwrite-asset {:org "babashka"
                                :repo "obb"
                                :file file
                                :tag (str "v" version)
                                :draft true
                                :prerelease (str/ends-with? version "SNAPSHOT")}))
      (println "Skipping release artifact (no GITHUB_TOKEN or not on main branch)"))
    nil))
