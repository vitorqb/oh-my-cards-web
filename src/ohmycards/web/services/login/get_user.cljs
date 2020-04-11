(ns ohmycards.web.services.login.get-user
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.user :as kws.user]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Services.Login.GetUser")

(defn- parse-get-user-response
  [{{:keys [email]} ::kws.http/body} token]
  {kws.user/email email kws.user/token token})

(defn- run-get-user-http-call!
  "Runs the http call to get a user from a token."
  [{:keys [http-fn]} token]
  (http-fn
   ::kws.http/url "/v1/auth/user"
   ::kws.http/method :get
   ::kws.http/token token))

(defn main!
  [opts token]
  (log "Getting user")
  (a/map #(parse-get-user-response % token) [(run-get-user-http-call! opts token)]))
