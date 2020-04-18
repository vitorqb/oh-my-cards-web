(ns ohmycards.web.services.cards-grid-profile-manager.impl.read
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.utils :as utils]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]))

(defn- parse-result
  "Parses the result from the load http call."
  [{success?
    ::kws.http/success?
    {name :name {:keys [excludeTags includeTags page pageSize query]} :config}
    ::kws.http/body}]
  (if success?
    {kws/success? true
     kws/fetched-profile {kws.profile/name name
                          kws.profile/config {kws.config/exclude-tags excludeTags
                                              kws.config/include-tags includeTags
                                              kws.config/page page
                                              kws.config/page-size pageSize
                                              kws.config/tags-filter-query query}}}
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

(defn profile-exists?
  "Checks whether a profile exists in the BE."
  [opts profile-name]
  (let [resp-chan (run-http-call! opts profile-name)]
    (letfn [(handle-unknown-error []
              (let [msg "Unexpected error when getting a profile."]
                (js/alert msg)
                (throw (js/Error. msg))))]
      (utils/with-out-chan [out]
        (a/go
          (case (kws.http/status (a/<! resp-chan))
            200 (a/>! out true)
            404 (a/close! out)
            (handle-unknown-error)))))))
