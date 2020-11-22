(ns ohmycards.web.services.http
  "Small wrapper around cljs-http providing http requests."
  (:require [cljs-http.client :as http]
            [cljs.core.async :as a]
            [ohmycards.web.globals :as globals]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.utils.logging :as log]))

(log/deflogger log "Services.Http")

(defn- parse-args
  "Parses the arguments for the cljs-http client."
  [{::kws.http/keys [method url json-params token query-params multipart-params] :as args}]
  (cond-> {:method method :url url :with-credentials? false}
    :always          (update :url #(str "/api" %))
    token            (assoc-in [:headers "Authorization"] (str "Bearer " (or (:value token) token)))
    json-params      (assoc :json-params json-params)
    query-params     (assoc :query-params query-params)
    multipart-params (assoc :multipart-params multipart-params)))

(defn- parse-response
  "Parses the response from teh cljs-http client."
  [{:keys [success body status]}]
  {::kws.http/success? success
   ::kws.http/status status
   ::kws.http/body body})

(defn- http
  "Runs an http request."
  [& {::kws.http/keys [method url json-params token] :as args}]
  (a/map parse-response [(http/request (parse-args args))]))

(defn run-action
  "Runs an `ohmycards.web.protocols.http/HttpAction`.
  `token` is used by default if the `HttpAction` does not provide a token."
  [action {::kws.http/keys [token] ::keys [http-fn]}]
  (let [http-fn (or http-fn http)
        args [kws.http/token (or (protocols.http/token action) token)
              kws.http/method (protocols.http/method action)
              kws.http/url (protocols.http/url action)
              kws.http/query-params (protocols.http/query-params action)
              kws.http/json-params (protocols.http/json-params action)
              kws.http/multipart-params (protocols.http/multipart-params action)]]
    (log "Sending http request for http action " action)
    (a/go
      (let [response (a/<! (apply http-fn args))
            parsed-response (if (kws.http/success? response)
                              (protocols.http/parse-success-response action response)
                              (protocols.http/parse-error-response action response))]
        (log "Received response for http action " {:action action :response response :parsed-response parsed-response})
        (protocols.http/do-after! action response parsed-response)
        parsed-response))))

(when globals/DEV 

  (def default-mock-response
    "Default response used by mock-run-action"
    {kws.http/success? true kws.http/status 200 kws.http/body {}})

  (defn mk-mock-run-action
    "Returns a function that can be used as `run-action`, but doesn't really
     perform any request. All hooks from HttpAction are supported and called.
     opts.calls - An atom that will receive all actions that have been run.
     opts.response - An http mock response."
    [{:keys [calls response]}]
    (let [response' (or response default-mock-response)
          http-fn (constantly (a/to-chan! [response']))]
      (fn mock-run-action [action]
        (when calls (swap! calls conj action))
        (run-action action {::http-fn http-fn})))))
