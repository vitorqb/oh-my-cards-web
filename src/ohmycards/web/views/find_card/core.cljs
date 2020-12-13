(ns ohmycards.web.views.find-card.core
  (:require [ohmycards.web.common.async-actions.core :as async-actions]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.find-card.core :as kws]
            [reagent.core :as r]))

(defn- submit-async-action
  "Returns the AsyncAction for submit"
  [{:keys [state] ::kws/keys [goto-displaycard! fetch-card!] :as props}]
  {kws.async-actions/pre-reducer-fn
   #(assoc % kws/disabled? true kws/error-message nil)

   kws.async-actions/state
   state

   kws.async-actions/action-fn
   #(-> % kws/value fetch-card!)

   kws.async-actions/post-reducer-fn
   (fn [state response]
     (if-let [error-message (kws.cards-crud/error-message response)]
       (assoc state kws/disabled? false kws/error-message error-message)
       (assoc state kws/disabled? false)))

   kws.async-actions/post-hook-fn
   (fn [response]
     (when-let [id (some-> response kws.cards-crud/read-card kws.card/id)]
       (goto-displaycard! id)))})

(defn- handle-submit!
  "Handles the submission of the form"
  [props]
  (async-actions/run (submit-async-action props)))

(defn init-state!
  "Initializes (or resets) the state."
  [{:keys [state] :as props}]
  (reset! state {kws/value ""
                 kws/disabled? false}))

(defn main
  "A view to find cards by id or ref"
  [{:keys [state] :as props}]
  (let [disabled? (kws/disabled? @state)]
    [:div.find-card
     [:div.find-card__title-wrapper
      [:h3 "Find Card"]]
     [:div.find-card__form-wrapper
      [form/main {::form/on-submit (fn [_] (handle-submit! props))}
       [form/row
        {:label "Id or Ref"
         :input [inputs/main
                 {kws.inputs/auto-focus true
                  kws.inputs/cursor (r/cursor state [kws/value])
                  kws.inputs/disabled? disabled?}]}]
       [:div.find-card__submit-wrapper
        [:input#submit {:type "submit"
                        :disabled disabled?}]]]]]))
