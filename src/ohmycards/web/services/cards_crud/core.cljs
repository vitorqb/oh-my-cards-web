(ns ohmycards.web.services.cards-crud.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.cards.core :as common.cards]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-crud.actions :as kws.actions]
            [ohmycards.web.kws.services.cards-crud.core :as kws]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.services.http.utils :as http.utils]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Services.CardsCrud")

;; Helpers
(defn- http-body->read-err [b]
  (http.utils/body->err-msg b "Could not read card!"))

(defn- http-body->delete-err [b]
  (http.utils/body->err-msg b "Error deleting card!"))

(defn- http-body->update-err [b]
  (http.utils/body->err-msg b "Error updating card!"))

;; Methods
(defmulti parse-response*
  "Multimethod that parses the http-response of a given CRUD action.
  If must return the response for clients."
  {:arglists '([action response])}
  (fn [action response] action))

(defmulti run-http-call!*
  "Multimethod that runs an http call for a CRUD action."
  {:arglist '([action request-opts])}
  (fn [action request-opts] action))

;; Created Impl
(defmethod parse-response* kws.actions/create
  [_ {::kws.http/keys [success? body]}]
  (if success?
    {kws/created-card (common.cards/from-http body)}
    {kws/error-message body}))

(defmethod run-http-call!* kws.actions/create
  [_ {:keys [http-fn] ::kws/keys [card-input]}]
  (http-fn
   kws.http/method :POST
   kws.http/url "/v1/cards"
   kws.http/json-params (-> card-input common.cards/to-http (dissoc :id))))

;; Read Impl
(defmethod parse-response* kws.actions/read
  [_ {::kws.http/keys [success? body]}]
  (if success?
    {kws/read-card (common.cards/from-http body)}
    {kws/error-message (http-body->read-err body)}))

(defmethod run-http-call!* kws.actions/read
  [_ {:keys [http-fn] ::kws/keys [card-id]}]
  (http-fn
   kws.http/method :GET
   kws.http/url (str "/v1/cards/" card-id)))

;; Update Impl
(defmethod parse-response* kws.actions/update
  [_ {::kws.http/keys [success? body]}]
  (if success?
    {kws/updated-card (common.cards/from-http body)}
    {kws/error-message (http-body->update-err body)}))

(defmethod run-http-call!* kws.actions/update
  [_ {:keys [http-fn] ::kws/keys [card-input]}]
  (http-fn
   kws.http/method :POST
   kws.http/url (str "/v1/cards/" (kws.card/id card-input))
   kws.http/json-params (-> card-input common.cards/to-http (dissoc :id))))

;; Delete Impl
(defmethod parse-response* kws.actions/delete
  [_ {::kws.http/keys [success? body]}]
  (if success?
    {}
    {kws/error-message (http-body->delete-err body)}))

(defmethod run-http-call!* kws.actions/delete
  [_ {:keys [http-fn] ::kws/keys [card-id]}]
  (http-fn
   kws.http/method :DELETE
   kws.http/url (str "/v1/cards/" card-id)))

;; Generic functions
(defn- run-action!
  "Run an action."
  [action opts]
  (log "Running action " action)
  (a/go
    (let [response (parse-response* action (a/<! (run-http-call!* action opts)))]
      (events-bus/send! action response)
      response)))

;; Public API
(defn create! [opts card-input]
  (run-action! kws.actions/create (assoc opts kws/card-input card-input)))

(defn read! [opts card-id]
  (run-action! kws.actions/read (assoc opts kws/card-id card-id)))

(defn update! [opts card-input]
  (run-action! kws.actions/update (assoc opts kws/card-input card-input)))

(defn delete! [opts card-id]
  (run-action! kws.actions/delete (assoc opts kws/card-id card-id)))
