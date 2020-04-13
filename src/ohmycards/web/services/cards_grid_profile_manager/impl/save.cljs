(ns ohmycards.web.services.cards-grid-profile-manager.impl.save
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]))

(defn- parse-result
  "Parses the result from the save http call."
  [{::kws.http/keys [success?]}]
  {kws/success? success?})

(defn- run-http-call!
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

;; Public
(defn main!
  "Async. saves a profile in the BE."
  [opts profile]
  (let [http-chan (run-http-call! opts profile)]
    (a/go (parse-result (a/<! http-chan)))))
