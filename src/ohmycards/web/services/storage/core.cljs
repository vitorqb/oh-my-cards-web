(ns ohmycards.web.services.storage.core
  (:require [ohmycards.web.common.storage.core :as storage]
            [ohmycards.web.kws.common.storage.core :as kws.storage]))

(def ^:private ^:dynamic *storage* nil)

(defn put!  [obj] (storage/put  *storage* obj))
(defn peek! [key] (storage/peek *storage* key))

(defn init!
  "Initializes the storage service creating and saving a storage instance."
  [{:keys [state]}]
  (set! *storage* (storage/new-storage {kws.storage/state state})))
