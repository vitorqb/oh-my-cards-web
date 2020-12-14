(ns ohmycards.web.services.storage.core
  (:require [ohmycards.web.services.logging.core :as logging]
            [ohmycards.web.common.storage.core :as storage]
            [ohmycards.web.kws.common.storage.core :as kws.storage]))

(logging/deflogger log "Services.Storage")

(def ^:private ^:dynamic *storage* nil)

(defn put!  [obj]
  (log "Putting obj into storage...")
  (storage/put  *storage* obj))

(defn peek! [key]
  (log (str "Retrieving obj with key " key))
  (storage/peek *storage* key))

(defn init!
  "Initializes the storage service creating and saving a storage instance."
  [{:keys [state]}]
  (set! *storage* (storage/new-storage {kws.storage/state state})))
