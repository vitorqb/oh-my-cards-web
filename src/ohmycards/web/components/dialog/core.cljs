(ns ohmycards.web.components.dialog.core
  (:require [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.components.dialog.core :as kws]))

(defn- header
  "The header of the dialog, with the exit button"
  [{:keys [state]}]
  [:div.dialog__header
   [:button.icon-button {:on-click #(swap! state assoc kws/active? false)}
    [icons/close]]])

(defn main
  "A dialog!"
  [{:keys [state] :as props} & children]
  (when (kws/active? @state)
    [:div.dialog {}
     [:div.dialog__contents
      [header props]
      (into [:div.dialog__body] children)]]))
