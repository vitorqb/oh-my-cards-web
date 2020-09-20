(ns ohmycards.web.kws.services.cards-grid-profile-manager.core)

;; Profile Response attrs
(def success? "Was the service fetch successfull?" ::success?)
(def fetched-profile "The fetched profile." ::fetched-profile)

;; Configuration
(def set-metadata-fn! "A function called when a new metadata is fetched" ::set-metadata-fn!)

;; Bus actions
(def action-new-grid-profile "A event bus action for a new grid profile fetched." ::action-new-grid-profile)
