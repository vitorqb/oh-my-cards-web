(ns ohmycards.web.services.http
  "Small wrapper around cljs-http providing http requests."
  (:require [cljs-http.client :as http]
            [cljs.core.async :as a]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.utils.logging :as log]))


(log/deflogger log "Services.Http")

(defn- parse-args
  "Parses the arguments for the cljs-http client."
  [{::kws.http/keys [method url json-params token query-params] :as args}]
  (cond-> {:method method :url url :with-credentials? false}
    :always      (update :url #(str "/api" %))
    token        (assoc-in [:headers "Authorization"] (str "Bearer " (or (:value token) token)))
    json-params  (assoc :json-params json-params)
    query-params (assoc :query-params query-params)))

(defn- parse-response
  "Parses the response from teh cljs-http client."
  [{:keys [success body status]}]
  {::kws.http/success? success
   ::kws.http/status status
   ::kws.http/body body})

;; !!!! TODO - Make private?
(defn http
  "Runs an http request."
  [& {::kws.http/keys [method url json-params token] :as args}]
  (a/map parse-response [(http/request (parse-args args))]))

(defn run-action
  "Runs an `ohmycards.web.protocols.http/HttpAction`"
  [action {::kws.http/keys [token]}]
  (let [args [kws.http/token token
              kws.http/method (protocols.http/method action)
              kws.http/url (protocols.http/url action)
              kws.http/query-params (protocols.http/query-params action)
              kws.http/json-params (protocols.http/json-params action)]]
    (log "Sending http request for http action " action)
    (a/go
      (let [response (a/<! (apply http args))
            parsed-response (if (kws.http/success? response)
                              (protocols.http/parse-success-response action response)
                              (protocols.http/parse-error-response action response))]
        (log "Received response for http action " {:action action :response response :parsed-response parsed-response})
        (protocols.http/do-after! action response parsed-response)
        parsed-response))))
