(ns ohmycards.web.services.cards-grid-profile-manager.impl.read
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.utils :as utils]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]))

;; Private
(defrecord ^:private ProfileExistsAction [profile-name]
  protocols.http/HttpAction
  (protocols.http/method [_] :GET)
  (protocols.http/url [_] (str "/v1/cards-grid-profile/" profile-name)))

(defrecord ^:private Action [profile-name]

  protocols.http/HttpAction

  (protocols.http/method [_] :GET)

  (protocols.http/url [_] (str "/v1/cards-grid-profile/" profile-name))

  (protocols.http/parse-error-response [_ response] {kws/success? false})

  (protocols.http/parse-success-response [_ response]
    (let [body (kws.http/body response)
          config (:config body)]
      {kws/success? true
       kws/fetched-profile {kws.profile/name (:name body)
                            kws.profile/config {kws.config/exclude-tags (:excludeTags config)
                                                kws.config/include-tags (:includeTags config)
                                                kws.config/page (:page config)
                                                kws.config/page-size (:pageSize config)
                                                kws.config/tags-filter-query (:query config)}}})))

;; Public
(defn main!
  "Loads a profile from the BE."
  [{:keys [run-http-action-fn]} profile-name]
  (-> profile-name ->Action run-http-action-fn))

(defn profile-exists?
  "Checks whether a profile exists in the BE."
  [{:keys [run-http-action-fn]} profile-name]
  (let [resp-chan (-> profile-name ->ProfileExistsAction run-http-action-fn)]
    (letfn [(handle-unknown-error []
              (let [msg "Unexpected error when getting a profile."]
                (js/alert msg)
                (throw (js/Error. msg))))]
      (utils/with-out-chan [out]
        (a/go
          (case (kws.http/status (a/<! resp-chan))
            200 (a/>! out true)
            404 (a/close! out)
            (handle-unknown-error)))))))
