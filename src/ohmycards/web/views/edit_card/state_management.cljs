(ns ohmycards.web.views.edit-card.state-management
  (:require [cljs.core.async :as a]
            [medley.core :as m]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.card :as kws.card]
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

(defn- should-fetch-card?
  "Given the current state and a card-id, decides whether we should go fetch the card or not."
  [state card-id]
  (and
   ;; If we have already fetched this card, skip
   (not= (::last-fetched-card-id state) card-id)
   ;; If we are fetching a card, skip
   (not (::is-fetching? state))))

(defn- reduce-before-card-fetch [state card-id]
  (assoc state
         ::is-fetching? true
         ::last-fetched-card-id card-id
         kws/loading? true
         kws/error-message nil
         kws/selected-card nil
         kws/card-input nil))

(defn- reduce-on-card-fetch [state {::kws.cards-crud/keys [read-card error-message]}]
  (cond-> (assoc state ::is-fetching? false kws/loading? false)
    error-message       (assoc kws/error-message error-message)
    (not error-message) (assoc kws/selected-card read-card
                               kws/card-input (card->form-input read-card))))

(defn init!
  "Initializes the state for edit-card.
  - `card-id`: The id of the card to edit.
  Returns the initializes state."
  [props card-id]
  (let [state       (:state props)
        fetch-card! (kws/fetch-card! props)]
    (when (should-fetch-card? @state card-id)
      (log (str "Initializing edit-card state for id " card-id))
      (swap! state reduce-before-card-fetch card-id)
      (a/go
        (swap! state reduce-on-card-fetch (a/<! (fetch-card! card-id)))))
    state))

(defn init-from-route-match!
  "Same as `init!`, but receives props and route-match."
  [props route-match]
  (init! props (-> route-match :parameters :query :id)))
