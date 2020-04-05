(ns ohmycards.web.services.cards-grid-profile-loader.core
  (:require [cljs.core.async :as async]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-loader.core :as kws]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Services.CardsGridProfileLoader")

;; Helpers
(defn- parse-result
  "Parses the result from the http call."
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

(defn- run-http-call!
  "Runs the http call for loading a profile."
  [{:keys [http-fn]} profile-name]
  (http-fn kws.http/method :GET
           kws.http/query-params {:profile-name profile-name}
           kws.http/url "/v1/cards-grid-profile"))

;; API
(defn main!
  "Asynchronously loads a profile from the BE."
  [opts profile-name]
  (log "Loading profile:" profile-name)
  (async/map parse-result [(run-http-call! opts profile-name)]))
