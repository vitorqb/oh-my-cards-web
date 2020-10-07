(ns ohmycards.web.services.card-history-fetcher.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.cards.history.core :as cards.history]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.card-history-fetcher.core :as kws]
            [ohmycards.web.services.http.utils :as services.http.utils]))

(defn- parse-response
  [response]
  (if (kws.http/success? response)
    {kws/success? true
     kws/history (-> response kws.http/body cards.history/from-http)}
    {kws/success? false
     kws/error-message (-> response kws.http/body (services.http.utils/body->err-msg nil))}))

(defn main
  "Fetches the history for a card."
  [id {:keys [http-fn]}]
  (let [response (http-fn kws.http/method :GET kws.http/url (str "/v1/cards/" id "/history"))]
    (a/map parse-response [response])))
