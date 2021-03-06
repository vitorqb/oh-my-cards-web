(ns ohmycards.web.views.find-card.core
  (:require [ohmycards.web.common.async-actions.core :as async-actions]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.header.core :as header]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.find-card.core :as kws]
            [reagent.core :as r]))

(def ^:const ^:private NOT_FOUND_ERR "Could not find any card!")

(defn- submit-async-action
  "Returns the AsyncAction for submit"
  [{:keys [state] ::kws/keys [goto-displaycard! fetch-card! storage-put!] :as props}]
  {kws.async-actions/pre-reducer-fn
   #(assoc % kws/disabled? true kws/error-message nil)

   kws.async-actions/state
   state

   kws.async-actions/action-fn
   #(-> % kws/value fetch-card!)

   kws.async-actions/post-reducer-fn
   (fn [state response]
     (if (kws.cards-crud/read-card response)
       (assoc state kws/disabled? false)
       (assoc state kws/disabled? false kws/error-message NOT_FOUND_ERR)))

   kws.async-actions/post-hook-fn
   (fn [response]
     (when-let [card (kws.cards-crud/read-card response)]
       (let [key (storage-put! card)]
         (goto-displaycard! (kws.card/id card) {:storage-key key}))))})

(defn- handle-submit!
  "Handles the submission of the form"
  [props]
  (async-actions/run (submit-async-action props)))

(defn init-state!
  "Initializes (or resets) the state."
  [{:keys [state] :as props}]
  (reset! state {kws/value ""
                 kws/disabled? false}))

(defn refresh!
  "Refreshes the state (on page re-entering)"
  [props]
  (init-state! props))

(defn main
  "A view to find cards by id or ref"
  [{:keys [state] ::kws/keys [goto-home!] :as props}]
  (let [disabled? (kws/disabled? @state)
        error-message (kws/error-message @state)]
    [:div.find-card
     [header/main
      {:left [:button#find-card-goto-home.icon-button {:on-click #(goto-home!)}
              [icons/arrow-left]]}]
     [:div.find-card__title-wrapper
      [:h3 "Find Card"]]
     [:div.find-card__form-wrapper
      [form/main {::form/on-submit (fn [_] (handle-submit! props))}
       [form/row
        {:label "Id or Ref"
         :input [inputs/main
                 {kws.inputs/auto-focus true
                  kws.inputs/cursor (r/cursor state [kws/value])}]}]
       [error-message-box/main {:value error-message}]
       [:div.find-card__submit-wrapper
        [:input#submit {:type "submit"
                        :disabled disabled?}]]]]]))
