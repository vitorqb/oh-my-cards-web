(ns ohmycards.web.services.login.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.lenses.login :as lenses.login]
            [ohmycards.web.kws.services.login.core :as kws]
            [ohmycards.web.kws.user :as kws.user]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.services.login.get-token :as get-token]
            [ohmycards.web.services.login.get-user :as get-user]
            [ohmycards.web.services.login.onetime-password :as onetime-password]
            [ohmycards.web.services.login.recover-token-from-cookie
             :as
             recover-token-from-cookie]
            [ohmycards.web.services.logging.core :as logging]))

;; Constants and declarations
(declare set-user!)

(logging/deflogger log "Services.Login")

(def ^:dynamic ^:private *state*
  "The login state, with the keys described in `ohmycards.web.kws.lenses.login`."
  nil)

(def ^:private onetime-password-url "/api/v1/auth/oneTimePassword")

(def actions
  "Possible login actions (interactions with BE)"
  #{kws/send-onetime-password-action kws/get-token-action})

;; Helper functions
(defn- try-login-from-cookies!
  "Tries to log the user in using the browser cookies."
  [opts]
  (a/go
    (log "Trying to log user in using cookies...")
    (when-let [token (a/<! (recover-token-from-cookie/main! opts))]
      (log "Token found: " token)
      (let [user (a/<! (get-user/main! opts token))]
        (set-user! user)))))

(defn- set-finished-loading! [state]
  (log "Login initialized.")
  (swap! state assoc lenses.login/initialized? true))

;; API
(defn init!
  "Initialization logic for the service. Tries to log the user in if he is not yet logged in.
  `opts.state`: An atom-like variable where we will store the login state.
  `opts.run-http-action`: A function used to run `ohmycards.web.protocols.http/HttpAction`."
  [{:keys [state] :as opts}]
  (log "Initializing...")
  (set! *state* state)
  (a/go
    (a/<! (try-login-from-cookies! opts))
    (set-finished-loading! state)))

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
  (if onetime-password
    (get-token/send! args opts)
    (onetime-password/send! args opts)))

(defn set-user!
  "Set's an user to the state."
  [user]
  (log "Setting new user: " user)
  (swap! *state* assoc lenses.login/current-user user)
  (events-bus/send! kws/new-user user))

(defn is-logged-in?
  "Returns true if an user is currently logged in."
  []
  (some? (lenses.login/current-user @*state*)))
