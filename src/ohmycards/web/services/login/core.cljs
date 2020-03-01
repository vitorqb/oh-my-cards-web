(ns ohmycards.web.services.login.core
  (:require  [cljs.core.async :as a]
             [ohmycards.web.kws.http :as kws.http]
             [ohmycards.web.services.login.onetime-password :as onetime-password]
             [ohmycards.web.kws.services.login.core :as kws]))

;; Constants
(def ^:private onetime-password-url "/api/v1/auth/oneTimePassword")

(def actions "Possible login actions (interactions with BE)"
  #{kws/send-onetime-password-action kws/get-token-action})

;; Functions
(defn- get-token!
  "Tries to get the token from the BE."
  [args opts]
  (js/console.log "Getting token...")
  ;; !!!! TODO
  nil)

(defn main
  "Performs login for a given user.
  - `email`: The user email.
  - `onetime-password`: The user onetime-password.
  Notice that if `onetime-password` is nil, we perform the login first step (send the
  password to the user by email). If it is non-nil, we perform the second step.
  Returns a channel with:
  `{::kws/error-message String ::kws/token token}`
  For `::kws/send-onetime-password`, `::kws/token` is always nil.
  For `::kws/get-token`, the `::kws/token` contains the token object."
  [{::kws/keys [onetime-password] :as args} opts]
  (js/console.log "Starting login service...")
  (if onetime-password
    (get-token! args opts)
    (onetime-password/send! args opts)))
