(ns layout
  "Move Emacs and Chrome side by side and take up entire screen")

(def finder (js/Application "Finder"))
(def bounds (-> (.-desktop finder)
                (.window)
                (.bounds)
                (js->clj :keywordize-keys true)))

(def half-width (/ (:width bounds) 2))

(defn set-x
  "Sets app to x coordinate and half of width of screen"
  [app x]
  (when-let [^js found (first (.windows app))]
    (.activate app)
    (set! (.-index found) 1)
    (set! (.-bounds found) (clj->js (assoc bounds :x x :width half-width)))))

(let [emacs (js/Application. "Emacs")
      chrome (js/Application. "Google Chrome")]
  (when (and (.-running emacs)
             (.-running chrome))
    (set-x emacs 0)
    (set-x chrome half-width)
    (.activate emacs)))
