(ns ohmycards.web.services.cards-grid-profile-manager.impl.create
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.services.cards-grid-profile-manager.impl.helpers :as helpers]))

(defn- parse-result
  "Parses the result from the save http call."
  [{::kws.http/keys [success?]}]
  {kws/success? success?})

(defn- run-http-call!
  "Runs the http call for saving a profile."
  [{:keys [http-fn]} profile]
  (http-fn kws.http/method :POST
           kws.http/url "/v1/cards-grid-profile"
           kws.http/json-params (helpers/serialize-profile profile)))

;; Public
(defn main!
  "Async. saves a profile in the BE."
  [opts profile]
  (let [http-chan (run-http-call! opts profile)]
    (a/go (parse-result (a/<! http-chan)))))
