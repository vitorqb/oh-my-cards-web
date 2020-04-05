(ns ohmycards.web.services.events-bus.core
  (:require [ohmycards.web.kws.services.events-bus.core :as kws]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Services.EventsBus")
(def ^:dynamic *handler* nil)

(defn init!
  "Initializes the event bus system."
  [{::kws/keys [handler]}]
  (log "Initializing...")
  (set! *handler* handler))

(defn send!
  "Sends an event to the bus."
  [event-kw args]
  (when (and *handler* event-kw)
    (log "Sending new event..." [event-kw args])
    (*handler* event-kw args)))
