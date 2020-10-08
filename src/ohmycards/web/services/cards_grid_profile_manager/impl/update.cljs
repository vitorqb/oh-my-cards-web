(ns ohmycards.web.services.cards-grid-profile-manager.impl.update
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.utils :as utils]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.cards-grid-profile-manager.impl.helpers
             :as
             helpers]))

(defrecord ^:private Action [profile]
  protocols.http/HttpAction
  (protocols.http/url [_] (str "/v1/cards-grid-profile/" (kws.profile/name profile)))
  (protocols.http/method [_] :POST)
  (protocols.http/json-params [_] (helpers/serialize-profile profile))
  (protocols.http/parse-success-response [_ _] {kws/success? true})
  (protocols.http/parse-error-response [_ _] {kws/success? false}))

;; Public
(defn main!
  "Updates a profile in the BE, based on it's name."
  [{:keys [run-http-action-fn]} profile]
  (-> profile ->Action run-http-action-fn))
