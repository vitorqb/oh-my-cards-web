(ns ohmycards.web.components.file-upload-dialog.core)

(defn main
  "A dialog used to upload files."
  [props]
  [:div "File upload dialog"])

(defn show!
  "Displays the file dialog and waits for the user to select a file.
   Returns a channel that will receive the selected file (or be closed)"
  [props]
  nil)

(defn hide!
  "Hides the file dialog"
  [props]
  nil)
