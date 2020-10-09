(ns ohmycards.web.views.edit-card.state-management
  (:require [cljs.core.async :as a]
            [medley.core :as m]
            [ohmycards.web.common.async-actions.core :as async-actions]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.common.coercion.result :as kws.coercion.result]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Views.EditCard.StateManagement")

(defn card->form-input
  "Transforms a Card data into the data for the input form of the edit card page."
  [card]
  (as-> card it
    (select-keys it [kws.card/id kws.card/body kws.card/title kws.card/tags kws.card/ref])
    (m/map-vals #(coercion.result/success % %) it)))

(defn form-input->card
  "Transforms a form input for edit card into the data for a card."
  [form-input]
  (m/map-vals kws.coercion.result/value form-input))

(defn- card-fetch-async-action [{:keys [state] ::kws/keys [fetch-card!]} card-id]
  {kws.async-actions/state
   state

   kws.async-actions/run-condition-fn
   #(and
     (not= (::last-fetched-card-id %) card-id)
     (not (::is-fetching? %)))

   kws.async-actions/pre-hook-fn
   #(log (str "Initializing edit-card state for id " card-id))

   kws.async-actions/pre-reducer-fn
   #(assoc %
          ::is-fetching? true
          ::last-fetched-card-id card-id
          kws/loading? true
          kws/error-message nil
          kws/selected-card nil
          kws/card-input nil)

   kws.async-actions/action-fn
   #(fetch-card! card-id)

   kws.async-actions/post-reducer-fn
   (fn [s {::kws.cards-crud/keys [read-card error-message]}]
     (cond-> (assoc s ::is-fetching? false kws/loading? false)
       error-message       (assoc kws/error-message error-message)
       (not error-message) (assoc kws/selected-card read-card
                                  kws/card-input (card->form-input read-card))))})

(defn init!
  "Initializes the state for edit-card.
  - `card-id`: The id of the card to edit.
  Returns the initializes state."
  [props card-id]
  (async-actions/run (card-fetch-async-action props card-id)))

(defn init-from-route-match!
  "Same as `init!`, but receives props and route-match."
  [props route-match]
  (init! props (-> route-match :parameters :query :id)))
