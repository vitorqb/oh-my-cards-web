(ns ohmycards.web.controllers.file-upload-dialog.core
  (:require [ohmycards.web.app.state :as app.state]
            [ohmycards.web.components.file-upload-dialog.core
             :as
             components.file-upload-dialog]))

(defn ^:private props
  "Returns the props for the file-upload-dialog instance."
  []
  {:state (app.state/state-cursor :controllers.file-upload-dialog)})

(defn show! [] (components.file-upload-dialog/show! (props)))
(defn hide! [] (components.file-upload-dialog/hide! (props)))

(defn component
  "The ReactJs component for this instance of file-upload-dialog"
  []
  [components.file-upload-dialog/main (props)])
