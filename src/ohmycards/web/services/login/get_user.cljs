(ns ohmycards.web.services.login.get-user
  (:require [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.user :as kws.user]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Services.Login.GetUser")

(defrecord Action [token]

  protocols.http/HttpAction

  (protocols.http/url [_] "/v1/auth/user")

  (protocols.http/method [_] :GET)

  (protocols.http/token [_] token)

  (protocols.http/parse-success-response [_ r]
    {kws.user/email (-> r kws.http/body :email)
     kws.user/token token})

  (protocols.http/parse-error-response [_ _]
    {}))

(defn main!
  "Fetches an user from the BE. Returns a channel with a map of `kws.user` keys."
  [opts token]
  (log "Getting user")
  ((:run-http-action-fn opts) (->Action token)))
