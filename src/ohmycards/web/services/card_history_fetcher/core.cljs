(ns ohmycards.web.services.card-history-fetcher.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.cards.history.core :as cards.history]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.card-history-fetcher.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.http.utils :as services.http.utils]))

(defrecord Action [id]

  protocols.http/HttpAction

  (protocols.http/url [_]
    (str "/v1/cards/" id "/history"))

  (protocols.http/parse-success-response [_ response]
    {kws/success? true
     kws/history (-> response kws.http/body cards.history/from-http)})

  (protocols.http/parse-error-response [_ response]
    {kws/success? false
     kws/error-message (-> response kws.http/body (services.http.utils/body->err-msg nil))}))
