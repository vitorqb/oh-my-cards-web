(ns ohmycards.web.services.cards-grid-profile-manager.impl.create
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.cards-grid-profile-manager.impl.helpers
             :as
             helpers]))

(defrecord Action [profile]
  protocols.http/HttpAction
  (protocols.http/url [_] "/v1/cards-grid-profile")
  (protocols.http/method [_] :POST)
  (protocols.http/json-params [_] (helpers/serialize-profile profile))
  (protocols.http/parse-error-response [_ _] {kws/success? false})
  (protocols.http/parse-success-response [_ _] {kws/success? true}))

(defn main!
  "Async. saves a profile in the BE."
  [{:keys [run-http-action-fn]} profile]
  (-> profile ->Action run-http-action-fn))
