(ns ohmycards.web.services.cards-grid-profile-manager.impl.update
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.utils :as utils]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.services.cards-grid-profile-manager.impl.helpers
             :as
             helpers]))

(defn- parse-response
  "Parses the update response."
  [{::kws.http/keys [success?]}]
  {kws/success? success?})

(defn- run-http-call!
  "Runs the http call for updating a profile."
  [{:keys [http-fn]} {profile-name ::kws.profile/name :as profile}]
  (http-fn kws.http/method :POST
           kws.http/url (str "/v1/cards-grid-profile/" profile-name)
           kws.http/json-params (helpers/serialize-profile profile)))

;; Public
(defn main!
  "Updates a profile in the BE, based on it's name."
  [opts profile]
  (let [resp-chan (run-http-call! opts profile)]
    (a/go (parse-response (a/<! resp-chan)))))
