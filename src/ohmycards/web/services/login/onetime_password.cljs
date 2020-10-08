(ns ohmycards.web.services.login.onetime-password
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.login.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Services.Login.OnetimePassword")

(defrecord Action [opts]

  protocols.http/HttpAction

  (protocols.http/method [_] :POST)

  (protocols.http/url [_] "/v1/auth/oneTimePassword")

  (protocols.http/json-params [_] {:email (kws/email opts)})

  (protocols.http/parse-error-response [_ _]
    {kws/action kws/send-onetime-password-action
     kws/error-message "Something went wrong when sending the one time password."})

  (protocols.http/parse-success-response [_ _]
    {kws/action kws/send-onetime-password-action}))

(defn send!
  "Asks BE to send a one time password to the user's email."
  [opts {:keys [run-http-action-fn]}]
  (log "Sending onetime password...")
  (run-http-action-fn (->Action opts)))
