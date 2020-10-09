(ns ohmycards.web.views.new-card.handlers.create-card
  (:refer-clojure :exclude [run!])
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.async-actions.core :as async-actions]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.services.cards-crud.core :as kws.services.cards-crud]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.views.new-card.queries :as queries]))

(def INVALID_FORM_MSG "ERROR: The form contains invalid data preventing the creation!")

(defn- create-async-action [{:keys [state] ::kws/keys [create-card!] :as props}]
  {kws.async-actions/state
   state

   kws.async-actions/run-condition-fn
   #(-> % kws/loading? not)

   kws.async-actions/pre-reducer-fn
   #(assoc % kws/loading? true kws/error-message nil)

   kws.async-actions/action-fn
   #(-> % queries/card-form-input create-card!)

   kws.async-actions/post-reducer-fn
   (fn [s {::kws.services.cards-crud/keys [error-message created-card]}]
     (cond-> s
       :always (assoc kws/loading? false)
       :always (dissoc kws/created-card)
       error-message (assoc kws/error-message error-message)
       created-card (assoc kws/created-card created-card)
       created-card (assoc kws/card-input {})))})

(defn- warn-user-of-invalid-input!
  "Warns the user about invalid inputs preventing the creation."
  [props]
  ((:notify! props) INVALID_FORM_MSG))

(defn main
  "Creates a card from the new-card props."
  [{:keys [state] :as props}]
  (when (-> @state kws/loading? not)
    (if (queries/form-has-errors? @state)
      (warn-user-of-invalid-input! props)
      (async-actions/run (create-async-action props)))))
