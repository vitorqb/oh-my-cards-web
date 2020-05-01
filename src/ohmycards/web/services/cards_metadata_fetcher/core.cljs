(ns ohmycards.web.services.cards-metadata-fetcher.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.utils.logging :as logging]))

(def ^:private url "/v1/metadata/cards")


(logging/deflogger log "Services.CardsMetadataFetcher")


(defn main
  "Fetches the metadata for the cards."
  [{:keys [http-fn]}]
  (log "Fetching metadata for cards...")
  (let [resp-chan (http-fn kws.http/method :GET kws.http/url url)]
    (a/go
     (let [resp (a/<! resp-chan)
           body (kws.http/body resp)
           tags (:tags body)]
       {kws.card-metadata/tags tags}))))
