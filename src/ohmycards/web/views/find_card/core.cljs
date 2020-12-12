(ns ohmycards.web.views.find-card.core
  (:require [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.views.find-card.core :as kws]
            [reagent.core :as r]))

(defn- handle-submit!
  "Handles the submission of the form"
  [props]
  nil)

(defn init-state!
  "Initializes (or resets) the state."
  [{:keys [state] :as props}]
  (reset! state {kws/value ""}))

(defn main
  "A view to find cards by id or ref"
  [{:keys [state] :as props}]
  [:div.find-card
   [:div.find-card__title-wrapper
    [:h3 "Find Card"]]
   [:div.find-card__form-wrapper
    [form/main {:on-submit (fn [_] (handle-submit! props))}
     [form/row
      {:label "Id or Ref"
       :input [inputs/main
               {kws.inputs/auto-focus true
                kws.inputs/cursor (r/cursor state [kws/value])}]}]
     [:div.find-card__submit-wrapper
      [:input {:type "submit"}]]]]])
