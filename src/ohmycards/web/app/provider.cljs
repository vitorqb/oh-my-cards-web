(ns ohmycards.web.app.provider
  "This namespace puts together different services and dependencies and provides them
  to the `core.cljs` and the contorllers."
  (:require [cljs.core.async :as async]
            [ohmycards.web.app.state :as app.state]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.lenses.login :as lenses.login]
            [ohmycards.web.kws.lenses.metadata :as lenses.metadata]
            [ohmycards.web.kws.user :as kws.user]
            [ohmycards.web.services.cards-crud.core :as services.cards-crud]
            [ohmycards.web.services.card-history-fetcher.core
             :as
             services.card-history-fetcher]
            [ohmycards.web.services.cards-metadata-fetcher.core
             :as
             services.cards-metadata-fetcher]
            [ohmycards.web.services.fetch-cards.core :as services.fetch-cards]
            [ohmycards.web.services.http :as services.http]
            [ohmycards.web.services.notify :as services.notify]
            [ohmycards.web.utils.clipboard :as utils.clipboard]))

(defn to-clipboard!
  "Sends a text to the clipboard and notifies user on completion."
  [txt]
  (utils.clipboard/to-clipboard! txt)
  (services.notify/notify! "Copied to clipboard!"))

;; !!!! TODO Remove me
(defn http-fn
  "Wraps the http service function injecting the token from the global state."
  [& args]
  (let [token (-> @app.state/state lenses.login/current-user kws.user/token)]
    (apply services.http/http kws.http/token token args)))

(defn run-http-action
  "Runs an `ohmycards.web.protocols.http/HttpAction`."
  [action]
  (let [token (-> @app.state/state lenses.login/current-user kws.user/token)
        opts  {kws.http/token token}]
    (services.http/run-action action opts)))

(defn fetch-cards!
  "A shortcut to fetch cards using the fetch-cards svc"
  [opts]
  (services.fetch-cards/main (assoc opts :http-fn http-fn)))

(defn fetch-card-history!
  "Fetches the history of a card."
  [id]
  (-> id services.card-history-fetcher/->Action run-http-action))

(defn fetch-card!
  "Fetches a single card."
  [id]
  (-> id services.cards-crud/->ReadAction run-http-action))

(defn create-card!
  "Creates a card"
  [card-input]
  (-> card-input services.cards-crud/->CreateAction run-http-action))

(defn update-card!
  "Updates a card"
  [card-input]
  (-> card-input services.cards-crud/->UpdateAction run-http-action))

(defn delete-card!
  "Deletes a card"
  [id]
  (-> id services.cards-crud/->DeleteAction run-http-action))

(defn notify!
  "Notifies the user of a msg."
  [msg]
  (services.notify/notify! msg))

(defn fetch-card-metadata
  "Fetches the metadata for cards."
  []
  (let [metadata-chan (run-http-action (services.cards-metadata-fetcher/->Action))]
    (async/go
      (swap! app.state/state assoc lenses.metadata/cards (async/<! metadata-chan)))))
