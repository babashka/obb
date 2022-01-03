#!/usr/bin/env obb

(def app (js/Application.currentApplication))

(set! (.-includeStandardAdditions app) true)

(def images
  (.chooseFile app
               #js {:withPrompt "Please select some images to process:"
                    :ofType #js ["public.image"]
                    :multipleSelectionsAllowed true}))

;; Update the initial progress information
(set! (.-totalUnitCount js/Progress) (count images))
(set! (.-completedUnitCount js/Progress) 0)
(set! (.-description js/Progress) "Processing images...")
(set! (.-additionalDescription js/Progress) "Preparing to process.")

(doseq [[i item] (map-indexed vector images)]
  ;; Update the progress detail
  (set! (.-additionalDescription js/Progress) (str "Processing image " i " of " (count images)))

  ;; Process the image

  ;; Increment the progress
  (set! (.-completedUnitCount js/Progress) i)

  ;; Pause for demonstration purposes, so progress can be seen
  (js/delay 1))

;; There’s no need to call a dedicated command to actually display progress
;; information. The act of setting values for the progress properties mentioned
;; above automatically results in progress information being displayed in a
;; dialog, Script Editor, or the menu bar.
