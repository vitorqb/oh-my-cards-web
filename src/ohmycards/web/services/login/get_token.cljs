(ns ohmycards.web.services.login.get-token
  (:require [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.login.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.logging.core :as logging]))

(logging/deflogger log "Services.Loging.GetToken")

(defrecord Action [opts]

  protocols.http/HttpAction

  (protocols.http/method [_] :POST)

  (protocols.http/url [_] "/v1/auth/token")

  (protocols.http/json-params [_]
    {:email (kws/email opts)
     :oneTimePassword (kws/onetime-password opts)})

  (protocols.http/parse-success-response [_ r]
    {kws/action kws/get-token-action
     kws/token  (kws.http/body r)})

  (protocols.http/parse-error-response [_ r]
    (let [status (-> r kws.http/status str)]
      (cond-> {kws/action kws/get-token-action}
        (re-matches #"5.." status)
        (assoc ::kws/error-message "Something went wrong.")

        (re-matches #"4.." status)
        (assoc ::kws/error-message "Invalid credentials.")))))

(defn send!
  "Sends a get-token request to the BE and parses the response."
  [opts {:keys [run-http-action-fn]}]
  (log "Asking for token...")
  (-> opts ->Action run-http-action-fn))
