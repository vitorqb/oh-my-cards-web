(ns ohmycards.web.services.fetch-cards.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.cards.core :as common.cards]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.fetch-cards.core :as kws]
            [ohmycards.web.services.http.utils :as http.utils]))

(def ^:private default-page 1)
(def ^:private default-page-size 20)

(defn fetch-query-params
  "Prepares the query params for fetching cards."
  [{::kws/keys [page page-size include-tags]}]
  (cond-> {:page (or page default-page) :pageSize (or page-size default-page-size)}
    include-tags (assoc :tags (http.utils/list->query-arg include-tags))))

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
  [{:keys [http-fn] :as opts}]
  (http-fn
   kws.http/method :GET
   kws.http/url "/v1/cards"
   kws.http/query-params (fetch-query-params opts)))

(defn main
  "Fetches cards from BE.
  `opts.http-fn`: Http service used for sending the request."
  [opts]
  (a/map parse-fetch-response [(fetch! opts)]))
