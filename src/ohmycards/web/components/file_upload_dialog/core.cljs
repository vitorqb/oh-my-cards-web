(ns ohmycards.web.components.file-upload-dialog.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.components.dialog.core :as dialog]
            [ohmycards.web.kws.components.dialog.core :as kws.dialog]
            [reagent.core :as r]))

(declare hide!)

(defn- on-hide!
  "Handles event of hidding the dialog."
  [{:keys [state] :as props}]
  (when-let [selected-file-chan (::selected-file-chan @state)]
    (a/close! selected-file-chan)
    (swap! state assoc ::selected-file-chan nil)))

(defn- dialog-props [{:keys [state] :as props}]
  {:state (r/cursor state [::dialog])
   kws.dialog/on-hide! #(on-hide! props)})

(defn- on-change!
  "Handles a change for the file input"
  [{:keys [state] :as props} event]
  (let [file (some-> event .-target .-files first)]
    (when-let [file-chan (::selected-file-chan @state)]
      (a/go
        (when file
          (a/>! file-chan file))
        (hide! props)))))

(defn main
  "A dialog used to upload files."
  [props]
  [dialog/main (dialog-props props)
   [:input.file-upload-dialog
    {:type "file"
     :on-change #(on-change! props %)}]])

(defn show!
  "Displays the file dialog and waits for the user to select a file.
   Returns a channel that will receive the selected file (or be closed)"
  [{:keys [state] :as props}]
  (let [chan (a/chan 1)]
    (swap! state assoc ::selected-file-chan chan)
    (dialog/show! (dialog-props props))
    chan))

(defn hide!
  "Hides the file dialog"
  [{:keys [state] :as props}]
  (dialog/hide! (dialog-props props)))
