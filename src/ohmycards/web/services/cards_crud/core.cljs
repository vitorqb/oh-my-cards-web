(ns ohmycards.web.services.cards-crud.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-crud.core :as kws]
            [ohmycards.web.services.http.utils :as http.utils]))

;; Helpers
(defn- http-body->card [body]
  {kws.card/id (:id body) kws.card/body (:body body) kws.card/title (:title body)})

(defn- http-body->read-err [b]
  (http.utils/body->err-msg b "Could not read card!"))

(defn- http-body->delete-err [b]
  (http.utils/body->err-msg b "Error deleting card!"))

(defn- http-body->update-err [b]
  (http.utils/body->err-msg b "Error updating card!"))

;; Create
(defn- parse-create-response
  "Parses a response from a be call to create a card."
  [{::kws.http/keys [success? body]}]
  (if success?
    {kws/created-card (http-body->card body)}
    {kws/error-message body}))

(defn- run-create-call!
  "Performs the http call to create a card."
  [{:keys [http-fn]} card-input]
  (http-fn
   kws.http/method :POST
   kws.http/url "/v1/cards"
   kws.http/json-params {:title (kws.card/title card-input)
                         :body  (kws.card/body card-input)}))

(defn create!
  "Creates a card in the backend."
  [opts card-input]
  (js/console.log "Creating card...")
  (a/map parse-create-response [(run-create-call! opts card-input)]))

;; Read
(defn- parse-read-response
  "Parses a response from a be call to read a card."
  [{::kws.http/keys [success? body]}]
  (if success?
    {kws/read-card (http-body->card body)}
    {kws/error-message (http-body->read-err body)}))

(defn- run-read-call!
  "Performs the http call to read a card."
  [{:keys [http-fn]} card-id]
  (http-fn
   kws.http/method :GET
   kws.http/url (str "/v1/cards/" card-id)))

(defn read!
  "Reads a card from the backend."
  [opts card-id]
  (js/console.log (str "Reading card... " card-id))
  (a/map parse-read-response [(run-read-call! opts card-id)]))

;; Update
(defn- parse-update-response
  "Parses the response of a update http call."
  [{::kws.http/keys [success? body]}]
  (if success?
    {kws/updated-card (http-body->card body)}
    {kws/error-message (http-body->update-err body)}))

(defn- run-update-call!
  "Performs the http call to update a card."
  [{:keys [http-fn]} card-input]
  (http-fn
   kws.http/method :POST
   kws.http/url (str "/v1/cards/" (kws.card/id card-input))
   kws.http/json-params {:title (kws.card/title card-input)
                         :body (kws.card/body card-input)}))

(defn update!
  "Updates a card in the backend."
  [opts card-input]
  (js/console.log "Updating card...")
  (a/map parse-update-response [(run-update-call! opts card-input)]))

;; Delete
(defn- parse-delete-response
  "Parses the response of a delete http call."
  [{::kws.http/keys [success? body]}]
  (if success?
    {}
    {kws/error-message (http-body->delete-err body)}))

(defn- run-delete-call!
  "Performs the http call to delete a card."
  [{:keys [http-fn]} card-id]
  (http-fn
   kws.http/method :DELETE
   kws.http/url (str "/v1/cards/" card-id)))

(defn delete!
  "Deletes a card in the backend."
  [opts card-id]
  (js/console.log "Removing card...")
  (a/map parse-delete-response [(run-delete-call! opts card-id)]))
