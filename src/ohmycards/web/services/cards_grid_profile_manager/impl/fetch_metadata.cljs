(ns ohmycards.web.services.cards-grid-profile-manager.impl.fetch-metadata
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.cards-grid.metadata.core :as kws.cards-grid.metadata]
            [ohmycards.web.kws.http :as kws.http]))

(defn- parse-response
  "Parses the response into a `ohmycards.web.kws.cards-grid.metadata.core` map."
  [{{names :names} ::kws.http/body :as response}]
  {kws.cards-grid.metadata/profile-names names})

(defn- run-http-call!
  "Runs the http call to fetch the cards grid metadata."
  [{:keys [http-fn]}]
  (http-fn
   kws.http/url "/v1/metadata/cards-grid-profile/names"
   kws.http/method "get"))

(defn main!
  "Fetches the metadata from the BE and returns."
  [opts]
  (a/go (-> opts run-http-call! a/<! parse-response)))
