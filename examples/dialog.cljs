#!/usr/bin/env obb

(def app (js/Application.currentApplication))

(set! (.-includeStandardAdditions app) true)

(let [dialog-text (str "The current date and time is " (.currentDate app))]
  (.displayDialog app dialog-text)

  (let [response (.displayDialog app
                                 "What is your name?"
                                 #js {:defaultAnswer ""
                                      :withIcon "note"
                                      :buttons #js ["Cancel" "Continue"]
                                      :defaultButton "Continue"})]
    (.displayDialog app (str "Hello, " (.-textReturned response) "."))))
