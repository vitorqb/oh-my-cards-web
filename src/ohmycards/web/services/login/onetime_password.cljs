(ns ohmycards.web.services.login.onetime-password
  (:require [ohmycards.web.kws.http :as kws.http]
            [cljs.core.async :as a]
            [ohmycards.web.kws.services.login.core :as kws]))


(defn- parse-response
  "Parses the http response from a post to sendOnetimePassword."
  [{::kws.http/keys [success?]}]
  (cond-> {::kws/action ::kws/send-onetime-password-action}
    (not success?)
    (assoc ::kws/error-message "Something went wrong when sending the one time password.")))

(defn send!
  "Asks BE to send a one time password to the user's email."
  [{::kws/keys [email]} {:keys [http-fn]}]
  (js/console.log "Sending onetime password...")
  (a/map
   parse-response
   [(http-fn
     ::kws.http/method :post
     ::kws.http/url "/v1/auth/oneTimePassword"
     ::kws.http/json-params {:email email})]))
