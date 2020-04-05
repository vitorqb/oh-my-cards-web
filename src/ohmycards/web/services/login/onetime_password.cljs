(ns ohmycards.web.services.login.onetime-password
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.login.core :as kws]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Services.Login.OnetimePassword")

(defn- parse-response
  "Parses the http response from a post to sendOnetimePassword."
  [{::kws.http/keys [success?]}]
  (cond-> {::kws/action ::kws/send-onetime-password-action}
    (not success?)
    (assoc ::kws/error-message "Something went wrong when sending the one time password.")))

(defn send!
  "Asks BE to send a one time password to the user's email."
  [{::kws/keys [email]} {:keys [http-fn]}]
  (log "Sending onetime password...")
  (a/map
   parse-response
   [(http-fn
     ::kws.http/method :post
     ::kws.http/url "/v1/auth/oneTimePassword"
     ::kws.http/json-params {:email email})]))
