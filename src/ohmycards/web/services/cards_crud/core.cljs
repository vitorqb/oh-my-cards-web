(ns ohmycards.web.services.cards-crud.core
  (:require [ohmycards.web.kws.card :as kws.card]
            [cljs.core.async :as a]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-crud.core :as kws]))

(defn- parse-response
  "Parses a response from a be call."
  [{::kws.http/keys [success? body]}]
  (if success?
    {kws/created-card {kws.card/id (:id body)
                       kws.card/body (:body body)
                       kws.card/title (:title body)}}
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
  (a/map parse-response [(run-create-call! opts card-input)]))
