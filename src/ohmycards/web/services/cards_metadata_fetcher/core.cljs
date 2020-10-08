(ns ohmycards.web.services.cards-metadata-fetcher.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.utils.logging :as logging]))

(defrecord Action []
  protocols.http/HttpAction
  (protocols.http/url [_] "/v1/metadata/cards")
  (protocols.http/method [_] :GET)
  (protocols.http/parse-success-response [_ r] {kws.card-metadata/tags (-> r kws.http/body :tags)}))
