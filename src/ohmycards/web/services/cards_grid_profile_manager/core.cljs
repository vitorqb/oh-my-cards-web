(ns ohmycards.web.services.cards-grid-profile-manager.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.services.cards-grid-profile-manager.impl.create
             :as
             create]
            [ohmycards.web.services.cards-grid-profile-manager.impl.fetch-metadata
             :as
             fetch-metadata]
            [ohmycards.web.services.cards-grid-profile-manager.impl.read :as read]
            [ohmycards.web.services.cards-grid-profile-manager.impl.update
             :as
             update]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Services.CardsGridProfileLoader")

;; Helpers
(defn- fetch-metadata!*
  "Pure version of fetch-metadata!"
  [{::kws/keys [on-metadata-fetch] :as opts} do-fetch-metadata!]
  {:pre [(ifn? on-metadata-fetch)]}
  (a/go
    (-> opts do-fetch-metadata! a/<! on-metadata-fetch)))

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
  (read/main! opts profile-name))

(defn save!
  "Asynchronously saves a profile to the BE."
  [opts {profile-name ::kws.profile/name :as profile}]
  (log "Saving profile:" profile)
  (a/go
    (if (a/<! (read/profile-exists? opts profile-name))
      (do
        (log "Updating profile: " profile)
        (when (js/confirm (str "Are you sure you want to update this profile?"))
          (a/<! (update/main! opts profile))))
      (do
        (log "Creating profile: " profile)
        (a/<! (create/main! opts profile))))
    (fetch-metadata! opts)))
