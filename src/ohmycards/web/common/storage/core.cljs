(ns ohmycards.web.common.storage.core
  (:refer-clojure :exclude [peek])
  (:require [ohmycards.web.kws.common.storage.core :as kws]))

(defn new-storage
  "Returns a new instance of a storage."
  [{::kws/keys [state]}]
  {::state state
   ::gen-key-fn #(str (random-uuid))})

(defn put
  "Saves an object into the storage, returning it's key."
  [storage obj]
  (let [key ((::gen-key-fn storage))]
    (swap! (::state storage) assoc key obj)
    key))

(defn peek
  "Retrieves an object from it's key."
  [storage key]
  (let [obj (get @(::state storage) key)]
    (swap! (::state storage) dissoc key)
    obj))
