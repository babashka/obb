#!/usr/bin/env obb

(def app (js/Application.currentApplication))

(set! (.-includeStandardAdditions app) true)

(def fruit-choices #js ["Apple" "Banana" "Orange"])

(def favorite-fruit
  (.chooseFromList
   app
   fruit-choices
   #js {:withPrompt "Select your favorite fruit:"
        :defaultItems #js ["Apple"]}))

(prn favorite-fruit)
