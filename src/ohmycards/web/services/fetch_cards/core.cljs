(ns ohmycards.web.services.fetch-cards.core
  (:require [clojure.string :as string]
            [ohmycards.web.common.cards.core :as common.cards]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.fetch-cards.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.http.utils :as http.utils]))

(def ^:private default-page 1)
(def ^:private default-page-size 20)

(defrecord Action [opts]

  protocols.http/HttpAction

  (protocols.http/url [_] "/v1/cards")

  (protocols.http/method [_] :GET)

  (protocols.http/query-params [_]
    (let [config            (kws/config opts)
          search-term       (kws/search-term opts)
          page              (or (kws.config/page config) default-page)
          page-size         (or (kws.config/page-size config) default-page-size)
          include-tags      (kws.config/include-tags config)
          exclude-tags      (kws.config/exclude-tags config)
          tags-filter-query (kws.config/tags-filter-query config)]
      (cond-> {:page page :pageSize page-size}
        include-tags                      (assoc :tags (http.utils/list->query-arg include-tags))
        exclude-tags                      (assoc :tagsNot (http.utils/list->query-arg exclude-tags))
        tags-filter-query                 (assoc :query tags-filter-query)
        (not (string/blank? search-term)) (assoc :searchTerm search-term))))

  (protocols.http/parse-success-response [_ r]
    (let [body (kws.http/body r)]
      {kws/cards          (->> body :items (map common.cards/from-http))
       kws/page           (:page body)
       kws/page-size      (:pageSize body)
       kws/count-of-cards (:countOfItems body)}))

  (protocols.http/parse-error-response [_ r]
    {kws/error-message (or (kws.http/body r) "Unknown error")}))
