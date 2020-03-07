(ns ohmycards.web.services.login.core
  (:require
   [cljs.core.async :as a]
   [ohmycards.web.kws.http :as kws.http]
   [ohmycards.web.services.login.onetime-password :as onetime-password]
   [ohmycards.web.kws.services.login.core :as kws]
   [ohmycards.web.services.login.get-token :as get-token]
   [ohmycards.web.kws.http :as kws.http]
   [ohmycards.web.kws.lenses.login :as lenses.login]
   [ohmycards.web.kws.user :as kws.user]))

;; Constants
(def ^:private onetime-password-url "/api/v1/auth/oneTimePassword")

(def actions "Possible login actions (interactions with BE)"
  #{kws/send-onetime-password-action kws/get-token-action})

;; Functions
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
    (get-token/send! args opts)
    (onetime-password/send! args opts)))

(defn- parse-token-recovery-response
  [state {token ::kws.http/body success? ::kws.http/success?}]
  (cond-> state
    (and token success?)
    (assoc-in [lenses.login/current-user kws.user/token] token)))

(defn- recover-token-from-cookies!
  "Tries to recover the token from cookies and store it in the state atom."
  [{:keys [state http-fn]}]
  (js/console.log "Trying to recover token...")
  (a/map
   #(swap! state parse-token-recovery-response %)
   [(http-fn
     ::kws.http/url "/v1/auth/tokenRecovery"
     ::kws.http/method :post)]))

(defn- should-try-to-recover-user?
  "Defines whether we should try to recover the user from the token."
  [{{::kws.user/keys [email token]} ::lenses.login/current-user}]
  (and token (not email)))

(defn- parse-get-user-response
  [state {{:keys [email]} ::kws.http/body}]
  (assoc-in state [lenses.login/current-user kws.user/email] email))

(defn- get-user-from-token!
  "Tries to get the user information from the token."
  [{:keys [state http-fn]}]
  (let [token (-> @state ::lenses.login/current-user ::kws.user/token)]
    (js/console.log "Querying for user info...")
    (a/map
     #(swap! state parse-get-user-response %)
     [(http-fn
       ::kws.http/url "/v1/auth/user"
       ::kws.http/method :get
       ::kws.http/token token)])))

(defn init-state!
  "Performs initialization services for the login, trying to log the user in if he is not
  yet logged in.
  `opts.state`: An state atom to set the token.
  `opts.http-fn`: A function for making http requests."
  [{:keys [state http-fn] :as opts}]
  (js/console.log "Initializing login state...")
  (a/go
    (a/<! (recover-token-from-cookies! opts))
    (when (should-try-to-recover-user? @state)
      (a/<! (get-user-from-token! opts)))))
