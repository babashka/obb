#!/usr/bin/env obb

(def app (js/Application.currentApplication))

(set! (.-includeStandardAdditions app) true)

(.say app "Processing is complete.")

(.say app "Just what do you think you're doing Dave?"
      #js {:using "Alex"
           :speakingRate 140
           :pitch 42
           :modulation 60})
