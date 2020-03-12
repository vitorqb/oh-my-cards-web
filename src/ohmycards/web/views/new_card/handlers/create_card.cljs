(ns ohmycards.web.views.new-card.handlers.create-card
  (:require [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.services.cards-crud.core :as services.cards-crud]
            [cljs.core.async :as a]
            [ohmycards.web.kws.services.cards-crud.core :as kws.services.cards-crud]))

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

(defn main
  "Creates a card from the new-card props."
  [{:keys [http-fn state]}]
  (when (should-create? @state)
    (a/go
      (swap! state before-create)
      (let [res (a/<! (services.cards-crud/create! {:http-fn http-fn} (kws/card-input @state)))]
        (swap! state after-create res)))))
