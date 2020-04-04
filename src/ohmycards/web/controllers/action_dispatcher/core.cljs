(ns ohmycards.web.controllers.action-dispatcher.core
  (:require [ohmycards.web.components.action-dispatcher.core :as action-dispatcher]
            [ohmycards.web.components.dialog.core :as dialog]
            [ohmycards.web.kws.components.action-dispatcher.core
             :as
             kws.action-dispatcher]
            [ohmycards.web.kws.components.dialog.core :as kws.dialog]
            [reagent.core :as r]))

(declare close!)

;; Private dynamic variable with the controller opts.
(defonce ^:dynamic ^:private *controller* nil)

;; Helpers
(defn- log [& xs] (apply js/console.log "[Controllers.ActionDispatcher] " xs))

;; Impl
(defn- show*
  "Pure implementation of show!"
  [{::keys [dialog-state action-dispatcher-state]}]
  (swap! dialog-state assoc kws.dialog/active? true)
  (action-dispatcher/reset-state action-dispatcher-state))

(defn- close*
  "Pure implementation of close!"
  [{::keys [dialog-state]}]
  (swap! dialog-state assoc kws.dialog/active? false))

(defn- component*
  "Pure implementation of component"
  [{::keys [dialog-state action-dispatcher-state actions-dispatcher-hydra-options] :as controller}]
  [dialog/main {:state dialog-state}
   [action-dispatcher/main
    {:state action-dispatcher-state
     kws.action-dispatcher/actions-hydra-head actions-dispatcher-hydra-options
     kws.action-dispatcher/dispatch-action! (fn [f]
                                              (log "Handling action " f)
                                              (f)
                                              (close!))}]])

;; Public API
(defn init!
  "Initializes the action dispatcher controller."
  [{:keys [state actions-dispatcher-hydra-options]}]
  (let [controller {::dialog-state (r/cursor state [::dialog])
                    ::action-dispatcher-state (r/cursor state [::action-dispatcher])
                    ::actions-dispatcher-hydra-options actions-dispatcher-hydra-options}]
    (log "Initializing with " controller)
    (set! *controller* controller)))

(defn show!
  "Shows the action dispatcher for the user."
  []
  (log "Showing...")
  (show* *controller*))

(defn close!
  "Closes the action dispatcher for the user."
  []
  (log "Closing...")
  (close* *controller*))

(defn component
  "Mounts the component putting the dialog and the action-dispatcher together."
  []
  [component* *controller*])
