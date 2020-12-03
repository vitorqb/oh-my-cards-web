(ns ohmycards.web.components.file-upload-dialog.core
  (:require [ohmycards.web.components.dialog.core :as dialog]
            [reagent.core :as r]))

(defn- dialog-props [{:keys [state]}]
  {:state (r/cursor state [::dialog])})

(defn main
  "A dialog used to upload files."
  [props]
  [dialog/main (dialog-props props)
   [:input.file-upload-dialog {:type "file"}]])

(defn show!
  "Displays the file dialog and waits for the user to select a file.
   Returns a channel that will receive the selected file (or be closed)"
  [props]
  (dialog/show! (dialog-props props)))

(defn hide!
  "Hides the file dialog"
  [props]
  (dialog/hide! (dialog-props props)))
