(ns ohmycards.web.services.fetch-cards.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.services.fetch-cards.core :as kws]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.common.cards.core :as common.cards]))

(def ^:private default-page 1)
(def ^:private default-page-size 20)

(defn- parse-fetch-response
  [{::kws.http/keys [success? body]}]
  (if success?
    {kws/cards          (->> body :items (map common.cards/from-http))
     kws/page           (:page body)
     kws/page-size      (:pageSize body)
     kws/count-of-cards (:countOfItems body)}
    {kws/error-message (or body "Unknown error")}))

(defn- fetch!
  "Runs the http call to fetch the cards."
  [{:keys [http-fn] ::kws/keys [page page-size]}]
  (http-fn
   kws.http/method :GET
   kws.http/url "/v1/cards"
   kws.http/query-params {:page (or page default-page)
                          :pageSize (or page-size default-page-size)}))

(defn main
  "Fetches cards from BE.
  `opts.http-fn`: Http service used for sending the request."
  [opts]
  (a/map parse-fetch-response [(fetch! opts)]))
