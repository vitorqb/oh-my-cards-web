(ns ohmycards.web.services.cards-crud.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.cards.core :as common.cards]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-crud.actions :as kws.actions]
            [ohmycards.web.kws.services.cards-crud.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.services.http.utils :as http.utils]
            [ohmycards.web.services.logging.core :as logging]))

(logging/deflogger log "Services.CardsCrud")

;; Helpers
(defn- http-body->read-err [b]
  (http.utils/body->err-msg b "Could not read card!"))

(defn- http-body->delete-err [b]
  (http.utils/body->err-msg b "Error deleting card!"))

(defn- http-body->update-err [b]
  (http.utils/body->err-msg b "Error updating card!"))

;; Public API
(defrecord CreateAction [card-input]

  protocols.http/HttpAction

  (protocols.http/url [_]
    "/v1/cards")

  (protocols.http/method [_]
    :POST)

  (protocols.http/json-params [_]
    (-> card-input common.cards/to-http (dissoc :id)))

  (protocols.http/parse-success-response [_ response]
    {kws/created-card (-> response kws.http/body common.cards/from-http)})

  (protocols.http/parse-error-response [_ response]
    {kws/error-message (kws.http/body response)})

  (protocols.http/do-after! [_ _ parsed-response]
    (events-bus/send! kws.actions/create parsed-response)))


(defrecord ReadAction [card-id]

  protocols.http/HttpAction

  (protocols.http/url [_]
    (str "/v1/cards/" card-id))

  (protocols.http/method [_]
    :GET)

  (protocols.http/parse-success-response [_ response]
    {kws/read-card (-> response kws.http/body common.cards/from-http)})

  (protocols.http/parse-error-response [_ response]
    {kws/error-message (-> response kws.http/body http-body->read-err)})

  (protocols.http/do-after! [_ _ parsed-response]
    (events-bus/send! kws.actions/read parsed-response)))


(defrecord UpdateAction [card-input]

  protocols.http/HttpAction

  (protocols.http/url [_]
    (str "/v1/cards/" (kws.card/id card-input)))

  (protocols.http/method [_]
    :POST)

  (protocols.http/json-params [_]
    (common.cards/to-http card-input))

  (protocols.http/parse-success-response [_ response]
    {kws/updated-card (-> response kws.http/body common.cards/from-http)})

  (protocols.http/parse-error-response [_ response]
    {kws/error-message (-> response kws.http/body http-body->update-err)})

  (protocols.http/do-after! [_ _ parsed-response]
    (events-bus/send! kws.actions/update parsed-response)))


(defrecord DeleteAction [card-id]

  protocols.http/HttpAction

  (protocols.http/url [_]
    (str "/v1/cards/" card-id))

  (protocols.http/method [_]
    :DELETE)

  (protocols.http/parse-success-response [_ response]
    {})

  (protocols.http/parse-error-response [_ response]
    {kws/error-message (-> response kws.http/body http-body->delete-err)})

  (protocols.http/do-after! [_ _ parsed-response]
    (events-bus/send! kws.actions/delete parsed-response)))
