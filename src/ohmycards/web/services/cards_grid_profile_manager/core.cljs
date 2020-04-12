(ns ohmycards.web.services.cards-grid-profile-manager.core
  (:require [cljs.core.async :as async]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.services.cards-grid-profile-manager.fetch-metadata
             :as
             fetch-metadata]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Services.CardsGridProfileLoader")

;; Helpers
(defn- parse-load-result
  "Parses the result from the load http call."
  [{success? ::kws.http/success?
    {name :name {:keys [excludeTags includeTags page pageSize]} :config} ::kws.http/body}]
  (if success?
    {kws/success? true
     kws/fetched-profile {kws.profile/name name
                          kws.profile/config {kws.config/exclude-tags excludeTags
                                              kws.config/include-tags includeTags
                                              kws.config/page page
                                              kws.config/page-size pageSize}}}
    {kws/success? false}))

(defn- parse-save-result
  "Parses the result from the save http call."
  [{::kws.http/keys [success?]}]
  {kws/success? success?})

(defn- run-load-http-call!
  "Runs the http call for loading a profile."
  [{:keys [http-fn]} profile-name]
  (http-fn kws.http/method :GET
           kws.http/url (str "/v1/cards-grid-profile/" profile-name)))

(defn- run-save-http-call!
  "Runs the http call for saving a profile."
  [{:keys [http-fn]}
   {{::kws.config/keys [page page-size include-tags exclude-tags]} ::kws.profile/config
    name ::kws.profile/name}]
  (http-fn kws.http/method :POST
           kws.http/url "/v1/cards-grid-profile"
           kws.http/json-params {:name name
                                 :config {:page page
                                          :pageSize page-size
                                          :includeTags include-tags
                                          :excludeTags exclude-tags}}))

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
  (async/map parse-load-result [(run-load-http-call! opts profile-name)]))

(defn save!
  "Asynchronously saves a profile to the BE."
  [opts profile]
  (log "Saving profile:" profile)
  (async/go
    (async/<! (async/map parse-save-result [(run-save-http-call! opts profile)]))
    (async/<! (fetch-metadata! opts))))