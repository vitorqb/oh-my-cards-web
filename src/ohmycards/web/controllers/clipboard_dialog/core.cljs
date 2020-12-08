(ns ohmycards.web.controllers.clipboard-dialog.core
  (:require [ohmycards.web.app.provider :as app.provider]
            [ohmycards.web.app.state :as app.state]
            [ohmycards.web.components.clipboard-dialog.core
             :as
             components.clipboard-dialog]
            [ohmycards.web.kws.components.clipboard-dialog.core
             :as
             kws.clipboard-dialog]))

(def ^:private props
  {:state (app.state/state-cursor :controllers.clipboard-dialog)
   kws.clipboard-dialog/to-clipboard! app.provider/to-clipboard!})

(defn component []
  [components.clipboard-dialog/main props])

(defn show! [text]
  (components.clipboard-dialog/show! props text))
