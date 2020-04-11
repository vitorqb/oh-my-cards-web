(ns ohmycards.web.services.login.get-user
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.user :as kws.user]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Services.Login.GetUser")

(defn- parse-response
  [{{:keys [email]} ::kws.http/body} token]
  {kws.user/email email kws.user/token token})

(defn- run-http-call!
  "Runs the http call to get a user from a token."
  [{:keys [http-fn]} token]
  (http-fn
   ::kws.http/url "/v1/auth/user"
   ::kws.http/method :get
   ::kws.http/token token))

(defn main!*
  "Pure version of main!"
  [opts token run-http-call!]
  (a/map #(parse-response % token) [(run-http-call! opts token)]))

(defn main!
  "Fetches an user from the BE. Returns a channel with a map of `kws.user` keys."
  [opts token]
  (log "Getting user")
  (main!* opts token run-http-call!))
