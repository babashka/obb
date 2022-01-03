#!/usr/bin/env obb

(def app (js/Application.currentApplication))

(set! (.-includeStandardAdditions app) true)

(.displayNotification app
                      "All graphics have been converted."
                      #js {:withTitle "My Graphic Processing Script"
                           :subtitle "Processing is complete."
                           :soundName "Frog"})
