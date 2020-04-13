(ns ohmycards.web.services.cards-grid-profile-manager.impl.load
  (:require [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [cljs.core.async :as a]))

(defn- parse-result
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

(defn- run-http-call!
  "Runs the http call for loading a profile."
  [{:keys [http-fn]} profile-name]
  (http-fn kws.http/method :GET
           kws.http/url (str "/v1/cards-grid-profile/" profile-name)))

;; Public
(defn main!
  "Loads a profile from the BE."
  [opts profile-name]
  (let [resp-chan (run-http-call! opts profile-name)]
    (a/go
      (parse-result (a/<! resp-chan)))))
