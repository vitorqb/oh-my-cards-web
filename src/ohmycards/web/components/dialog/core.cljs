(ns ohmycards.web.components.dialog.core
  (:require [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.components.dialog.core :as kws]))

(defn show!
  "Displayes the dialog."
  [{:keys [state] ::kws/keys [on-show!]}]
  (swap! state assoc kws/active? true)
  (when on-show!
    (on-show!)))

(defn hide!
  "Hidens the dialog."
  [{:keys [state] ::kws/keys [on-hide!]}]
  (swap! state assoc kws/active? false)
  (when on-hide!
    (on-hide!)))

(defn- header
  "The header of the dialog, with the exit button"
  [props]
  [:div.dialog__header
   [:button.icon-button {:on-click #(hide! props)}
    [icons/close]]])

(defn main
  "A dialog!"
  [{:keys [state] :as props} & children]
  (when (kws/active? @state)
    [:div.dialog {}
     [:div.dialog__contents
      [header props]
      (into [:div.dialog__body] children)]]))
