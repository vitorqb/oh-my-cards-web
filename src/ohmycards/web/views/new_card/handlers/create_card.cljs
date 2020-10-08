(ns ohmycards.web.views.new-card.handlers.create-card
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.services.cards-crud.core :as kws.services.cards-crud]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.views.new-card.queries :as queries])
  (:refer-clojure :exclude [run!]))

(def INVALID_FORM_MSG "ERROR: The form contains invalid data preventing the creation!")

(defn- should-create?
  "Given the current state, returns true if a create attempt can be performed."
  [state]
  (-> state kws/loading? boolean not))

(defn- before-create
  "Reducer for before the creation"
  [state]
  (assoc state
         kws/loading? true
         kws/error-message nil))

(defn- after-create
  "Reducer for after the creation"
  [state {::kws.services.cards-crud/keys [error-message created-card]}]
  (cond-> state
    :always (assoc kws/loading? false)
    :always (dissoc kws/created-card)
    error-message (assoc kws/error-message error-message)
    created-card (assoc kws/created-card created-card)
    created-card (assoc kws/card-input {})))

(defn- run!
  "Runs the creation."
  [{:keys [state] ::kws/keys [create-card!] :as props}]
  (let [card-form-input (queries/card-form-input props)]
    (a/go
      (swap! state before-create)
      (let [res (a/<! (create-card! card-form-input))]
        (swap! state after-create res)))))

(defn- warn-user-of-invalid-input!
  "Warns the user about invalid inputs preventing the creation."
  [props]
  ((:notify! props) INVALID_FORM_MSG))

(defn main
  "Creates a card from the new-card props."
  [{:keys [state] :as props}]
  (when (should-create? @state)
    (if (queries/form-has-errors? props)
      (warn-user-of-invalid-input! props)
      (run! props))))
