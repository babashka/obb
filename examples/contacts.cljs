#!/usr/bin/env obb

(def app (js/Application.currentApplication))

(set! (.-includeStandardAdditions app) true)

(def contacts (js/Application "Contacts"))

(let [match (-> contacts
                (.-people)
                (.whose (clj->js {:firstName "Rich" :lastName "Hickey"}))
                (aget 0)
                (.-emails)
                (aget 0))]
  (when (.exists match)
    (.value match)))
