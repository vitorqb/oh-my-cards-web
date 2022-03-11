(ns ohmycards.web.app.dialogs.copy-card-link
  (:require [ohmycards.web.components.copy-card-link-dialog.core :as copy-card-link-dialog]
            [ohmycards.web.kws.components.copy-card-link-dialog.core :as kws.copy-card-link-dialog]))

(defonce ^:private ^:dynamic *props* nil)

(defn show! [] (copy-card-link-dialog/show! *props*))
(defn hide! [] (copy-card-link-dialog/hide! *props*))

(defn dialog []
  [copy-card-link-dialog/main *props*])

(defn init! [{:keys [state to-clipboard!]}]
  (set! *props* {:state state
                 kws.copy-card-link-dialog/to-clipboard! to-clipboard!}))
