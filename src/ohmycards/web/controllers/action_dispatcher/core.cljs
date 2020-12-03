(ns ohmycards.web.controllers.action-dispatcher.core
  (:require [ohmycards.web.components.action-dispatcher.core :as action-dispatcher]
            [ohmycards.web.components.dialog.core :as dialog]
            [ohmycards.web.kws.components.action-dispatcher.core
             :as
             kws.action-dispatcher]
            [ohmycards.web.kws.components.dialog.core :as kws.dialog]
            [ohmycards.web.utils.logging :as utils.logging]
            [reagent.core :as r]))

(declare close!)
(utils.logging/deflogger log "Controllers.ActionDispatcher")

;; Private dynamic variable with the controller opts.
(defonce ^:dynamic ^:private *controller* nil)

;; Impl
(defn- dialog-props [controller]
  {:state (::dialog-state controller)})

(defn- show*
  "Pure implementation of show!
  - `controller-opts` is the options for the controller.
  - `hydra-head` is the hydra head that must be displayed."
  [controller-opts hydra-head]
  (when hydra-head
    (reset! (::current-hydra-head-atom controller-opts) hydra-head)
    (dialog/show! (dialog-props controller-opts))
    (action-dispatcher/reset-state (::action-dispatcher-state controller-opts))))

(defn- close*
  "Pure implementation of close!"
  [controller-opts]
  (dialog/hide! (dialog-props controller-opts)))

(defn- component*
  "Pure implementation of component"
  [{::keys [dialog-state action-dispatcher-state current-hydra-head-atom] :as controller}]
  [dialog/main {:state dialog-state}
   [action-dispatcher/main
    {:state action-dispatcher-state
     kws.action-dispatcher/actions-hydra-head @current-hydra-head-atom
     kws.action-dispatcher/dispatch-action! (fn [f]
                                              (log "Handling action " f)
                                              (f)
                                              (close!))}]])

;; Public API
(defn init!
  "Initializes the action dispatcher controller."
  [{:keys [state]}]
  (let [controller {::dialog-state            (r/cursor state [::dialog])
                    ::action-dispatcher-state (r/cursor state [::action-dispatcher])
                    ::current-hydra-head-atom (r/cursor state [::current-hydra-head])}]
    (log "Initializing with " controller)
    (set! *controller* controller)))

(defn show!
  "Shows the action dispatcher for the user."
  ([hydra-head]
   (log "Showing for..." hydra-head)
   (show* *controller* hydra-head)))

(defn close!
  "Closes the action dispatcher for the user."
  []
  (log "Closing...")
  (close* *controller*))

(defn component
  "Mounts the component putting the dialog and the action-dispatcher together."
  []
  [component* *controller*])
