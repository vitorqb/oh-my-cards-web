(ns ohmycards.web.components.dialog.core
  (:require [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.components.dialog.core :as kws]))

(defn show!
  "Displayes the dialog."
  [{:keys [state] ::kws/keys [on-show!]}]
  (swap! state assoc
         kws/active? true
         kws/last-active-element js/document.activeElement)
  (when on-show!
    (on-show!)))

(defn is-visible?
  "Returns true if the dialog is currently visible."
  [{:keys [state]}]
  (kws/active? @state))

(defn hide!
  "Hidens the dialog."
  [{:keys [state] ::kws/keys [on-hide!]}]
  (when-let [last-active-element (kws/last-active-element @state)]
    (.focus last-active-element))
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
