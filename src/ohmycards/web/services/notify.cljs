(ns ohmycards.web.services.notify
  "Provides a small toast API to notify the user of small msgs"
  (:require [cljs.core.async :as a]
            [ohmycards.web.utils.logging :as logging]
            [reagent.dom :as r.dom]))

(logging/deflogger log "Services.Notify")

(defonce ^:private ^:dynamic *state* nil)

(def TOAST_TIMEOUT_MS 3000)

;; Component and helpers
(defn- get-classes
  "Returns the classes for the toast"
  [state]
  (if (some-> state ::show?)
    "toast toast--show"
    "toast"))

(defn- schedule-hide!
  "Schedules the toast to be hidden in x ms"
  ([]
   (schedule-hide! *state*))
  ([state]
   (a/go
     (a/<! (a/timeout TOAST_TIMEOUT_MS))
     (swap! state assoc ::show? false))))

;; Main API
(defn init!
  "Initializes the service.
  - `state` - An atom-like where to keep the toast state."
  [{:keys [state] :as opts}]
  (log "Initializing with " opts)
  (set! *state* state))

(defn notify!
  "Notifies the user of a short msg using a toast.
  Demands the `toast` to be mounted "
  ([msg]
   (notify! msg *state*))
  ([msg state]
   (swap! state assoc ::msg msg ::show? true)
   (schedule-hide! state)))

(defn toast
  "The toast component. Needs to be mounted anywhere in the app tree."
  [_]
  (let [state' @*state*]
    [:div {:class (get-classes state')}
     [:span.toast__text (::msg state')]]))
