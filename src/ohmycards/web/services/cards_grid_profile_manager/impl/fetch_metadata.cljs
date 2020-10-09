(ns ohmycards.web.services.cards-grid-profile-manager.impl.fetch-metadata
  (:require [ohmycards.web.kws.cards-grid.metadata.core :as kws.cards-grid.metadata]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.protocols.http :as protocols.http]))

(defrecord Action []
  protocols.http/HttpAction

  (protocols.http/url [_] "/v1/metadata/cards-grid-profile/names")

  (protocols.http/method [_] :GET)

  (protocols.http/parse-success-response [_ response]
    {kws.cards-grid.metadata/profile-names (-> response kws.http/body :names)}))

(defn main!
  "Fetches the metadata from the BE and returns."
  [{:keys [run-http-action-fn]}]
  (run-http-action-fn (->Action)))
