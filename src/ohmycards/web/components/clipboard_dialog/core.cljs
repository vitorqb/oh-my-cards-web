(ns ohmycards.web.components.clipboard-dialog.core
  (:require [ohmycards.web.components.dialog.core :as dialog]
            [reagent.core :as r]
            [ohmycards.web.kws.components.clipboard-dialog.core :as kws]))

(defn- dialog-props [{:keys [state]}]
  {:state (r/cursor state [::dialog])})

(defn- on-copy!
  "Callback for when the user clicks the button to copy the text"
  [{::kws/keys [to-clipboard!] :keys [state] :as props}]
  (to-clipboard! (kws/text @state))
  (dialog/hide! (dialog-props props)))

(defn show!
  "Displays the dialog to the user."
  [{:keys [state] :as props} text]
  (swap! state assoc kws/text text)
  (dialog/show! (dialog-props props)))

(defn main
  "A helper dialog for the user to copy some text into it's clipboard."
  [{:keys [state] :as props}]
  (let [text (kws/text @state)]
    [dialog/main (dialog-props props)
     [:div.clipboard-dialog
      [:div
       [:textarea.clipboard-dialog__textarea {:value text :readOnly true}]]
      [:div.clipboard-dialog__button-wrapper
       [:button {:on-click #(on-copy! props)
                 :autoFocus true}
        "Copy to Clipboard!"]]]]))
