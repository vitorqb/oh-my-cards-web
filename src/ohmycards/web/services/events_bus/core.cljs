(ns ohmycards.web.services.events-bus.core
  (:require [ohmycards.web.kws.services.events-bus.core :as kws]))

(def ^:dynamic *handler* nil)

(defn init!
  "Initializes the event bus system."
  [{::kws/keys [handler]}]
  (js/console.log "Initializing events-bus system...")
  (set! *handler* handler))

(defn send!
  "Sends an event to the bus."
  [event-kw args]
  (when (and *handler* event-kw)
    (js/console.log "Sending new event to the bus..." [event-kw args])
    (*handler* event-kw args)))
