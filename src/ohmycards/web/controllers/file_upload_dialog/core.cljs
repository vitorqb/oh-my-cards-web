(ns ohmycards.web.controllers.file-upload-dialog.core
  (:require [ohmycards.web.app.provider :as app.provider]
            [ohmycards.web.app.state :as app.state]
            [ohmycards.web.components.file-upload-dialog.core
             :as
             components.file-upload-dialog]
            [ohmycards.web.services.file-uploader :as services.file-uploader]))

(defn ^:private props
  "Returns the props for the file-upload-dialog instance."
  []
  {:state (app.state/state-cursor :controllers.file-upload-dialog)})

(defn- show! [] (components.file-upload-dialog/show! (props)))
(defn- hide! [] (components.file-upload-dialog/hide! (props)))
(defn upload-file!
  "Asks the user for a file and uploads it"
  []
  (services.file-uploader/ask-and-upload
   {::services.file-uploader/ask-for-file! show!
    ::services.file-uploader/notify! app.provider/notify!
    ::services.file-uploader/run-http-action app.provider/run-http-action
    ::services.file-uploader/to-clipboard! app.provider/to-clipboard!}))

(defn component
  "The ReactJs component for this instance of file-upload-dialog"
  []
  [components.file-upload-dialog/main (props)])
