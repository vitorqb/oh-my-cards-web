(ns ohmycards.web.services.http
  "Small wrapper around cljs-http providing http requests."
  (:require [cljs-http.client :as http]
            [ohmycards.web.kws.http :as kws.http]
            [cljs.core.async :as a]))

(defn- parse-args
  "Parses the arguments for the cljs-http client."
  [{::kws.http/keys [method url json-params] :as args}]
  (cond-> {:method method :url url :with-credentials? false}
    :always (update :url #(str "/api" %))
    json-params (assoc :json-params json-params)))

(defn- parse-response
  "Parses the response from teh cljs-http client."
  [{:keys [success body status]}]
  {::kws.http/success? success
   ::kws.http/status status
   ::kws.http/body body})

(defn http
  "Runs an http request."
  [& {::kws.http/keys [method url json-params] :as args}]
  (a/map parse-response [(http/request (parse-args args))]))

