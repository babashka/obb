#!/usr/bin/env obb

; First arg to this script has to be a Slack user token: https://api.slack.com/authentication/token-types#user
; Optional second arg is the status text while in the Pomodoro

; Running this script will
; - Set Slack notifications to snooze for the length of the Pomodoro
; - Set a status, either generic or custom
; - Send a global shortcut key to start a Pomodoro on the local app
; - `brew install tomighty` # The app expected to be installed

(let [app (js/Application.currentApplication)
      pomodoro-end (let [now (js/Date.)]
                     (int
                       (/ (.setMinutes now (+ 25 (.getMinutes now)))
                          1000)))
      slack-token (first *command-line-args*)
      snooze-url (str "https://slack.com/api/dnd.setSnooze?num_minutes=24&token=" slack-token)
      custom-status (clj->js
                      {:profile
                       {:status_text (or (second *command-line-args*) "In Pomodoro")
                        :status_emoji ":tomato:"
                        :status_expiration pomodoro-end}})
      custom-status-curl (str
                           "curl -X POST"
                           " -H 'Content-Type: application/json; charset=utf-8'"
                           " -H 'Authorization: Bearer " slack-token "' "
                           "https://slack.com/api/users.profile.set"
                           " -d '" (js/JSON.stringify custom-status) "'")]

  (when-not slack-token
    (throw "The first argument to this script has to be the Slack user token, starting with xoxp - https://api.slack.com/authentication/token-types#user"))

  (set! (.-includeStandardAdditions app) true)

  (.doShellScript app custom-status-curl)
  (.doShellScript app (str "curl '" snooze-url "'"))

  (->
   (js/Application "System Events")
   ; Tomighty has ^⌘p as the Start Pomodoro global key.
   ; $ brew install tomighty
   (.keystroke "p" (clj->js {:using ["command down" "control down"]}))))

