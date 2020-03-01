(ns ohmycards.web.services.login.get-token
  (:require [ohmycards.web.kws.services.login.core :as kws]
            [cljs.core.async :as a]
            [ohmycards.web.kws.http :as kws.http]))

(defn- parse-response
  [{::kws.http/keys [success? body status] :as resp}]
  (cond-> {::kws/action ::kws/get-token-action}
    success?
    (assoc ::kws/token body)

    (re-matches #"5.." (str status))
    (assoc ::kws/error-message "Something went wrong.")

    (re-matches #"4.." (str status))
    (assoc ::kws/error-message "Invalid credentials.")))

(defn send!
  "Sends a get-token request to the BE and parses the response."
  [{::kws/keys [email onetime-password]} {:keys [http-fn]}]
  (js/console.log "Asking for token...")
  (a/map
   parse-response
   [(http-fn
     ::kws.http/method :post
     ::kws.http/url "/v1/auth/token"
     ::kws.http/json-params {:email email :oneTimePassword onetime-password})]))
