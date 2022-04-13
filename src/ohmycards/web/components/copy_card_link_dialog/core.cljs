(ns ohmycards.web.components.copy-card-link-dialog.core
  (:require [ohmycards.web.components.dialog.core :as dialog]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.components.copy-card-link-dialog.core :as kws]
            [ohmycards.web.kws.components.dialog.core :as kws.dialog]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [reagent.core :as r]
            [ohmycards.web.common.utils :as utils]))

(defn- dialog-props [{:keys [state]}]
   {:state (r/cursor state [::dialog])})

(defn show! [{:keys [state] :as props}]
  (swap! state assoc kws/value "")
  (dialog/show! (dialog-props props)))

(defn hide! [props]
  (dialog/hide! (dialog-props props)))

(defn- handle-submit! [{:keys [state] ::kws/keys [to-clipboard!] :as props}]
  (to-clipboard! (utils/internal-link (str "/#/cards/display?ref=" (kws/value @state))))
  (hide! props))

(defn main
  "A dialog component that asks the user to enter a card reference and copies to the
  clipboard a link to it."
  [{:keys [state] :as props}]
  [dialog/main (dialog-props props)
   [:div.copy-card-link-dialog
    [form/main {::form/on-submit #(handle-submit! props)}
     [:span "Get link to card (by ref or id)"]
     [:div.copy-card-link-dialog__input-wrapper
      [inputs/main {kws.inputs/auto-focus true
                    kws.inputs/cursor (r/cursor state [kws/value])}]]]]])
