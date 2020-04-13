(ns ohmycards.web.services.cards-grid-profile-manager.core
  (:require [cljs.core.async :as async]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.services.cards-grid-profile-manager.impl.fetch-metadata
             :as
             fetch-metadata]
            [ohmycards.web.services.cards-grid-profile-manager.impl.load :as load]
            [ohmycards.web.services.cards-grid-profile-manager.impl.save :as save]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Services.CardsGridProfileLoader")

;; Helpers
(defn- fetch-metadata!*
  "Pure version of fetch-metadata!"
  [{::kws/keys [on-metadata-fetch] :as opts} do-fetch-metadata!]
  {:pre [(ifn? on-metadata-fetch)]}
  (async/go
    (-> opts do-fetch-metadata! async/<! on-metadata-fetch)))

;; API
(defn fetch-metadata!
  "Asynchronously loads the metadata for card grid profiles from the BE. This is needed,
  for example, every time a new user logs in."
  [opts]
  (log "Fetching metadata... ")
  (fetch-metadata!* opts fetch-metadata/main!))

(defn load!
  "Asynchronously loads a profile from the BE."
  [opts profile-name]
  (log "Loading profile:" profile-name)
  (load/main! opts profile-name))

(defn save!
  "Asynchronously saves a profile to the BE."
  [opts profile]
  (log "Saving profile:" profile)
  (async/go
    (async/<! (save/main! opts profile))
    (fetch-metadata! opts)))
