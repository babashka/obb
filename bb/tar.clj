(ns tar
  (:require [babashka.fs :as fs]
            [babashka.tasks :refer [shell]]))

(defn tar [{:keys [executable-name
                   executable-path
                   tar-file]}]
  (fs/with-temp-dir [tmp-dir {}]
    (fs/copy executable-path tmp-dir)
    (fs/copy "script/brew_install.sh" tmp-dir)
    (let [libexec (fs/file tmp-dir "libexec")]
      (fs/create-dirs libexec)
      (run! (fn [js-file]
              (when-not (contains? #{"obb.js" "inferred_externs.js"}
                                   (fs/file-name js-file))
                (fs/copy js-file (fs/file libexec (fs/file-name js-file)))))
            (fs/glob "out" "*.js"))
      (shell {:dir (str tmp-dir)}
             "tar -czvf" "obb.tar.gz" executable-name "brew_install.sh" "libexec")
      (fs/copy (fs/file tmp-dir "obb.tar.gz") tar-file {:replace-existing true}))))
