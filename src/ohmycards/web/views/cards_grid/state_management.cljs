(ns ohmycards.web.views.cards-grid.state-management
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.tags.core :as tags]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core
             :as
             kws.cards-grid-profile-manager]
            [ohmycards.web.kws.services.fetch-cards.core :as kws.fetch-cards]
            [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [ohmycards.web.utils.logging :as logging]
            [ohmycards.web.utils.pagination :as utils.pagination]))

(logging/deflogger log "Views.CardsGrid.StateManagement")

;; Helpers
(defn- state-initialized?
  "Returns a boolean indicating whether the state has been initialized."
  [{::kws.cards-grid/keys [status]}]
  (-> status nil? not))

(defn- reduce-on-fetched-cards
  "Reduces the state after cards have been fetched."
  [state {::kws.fetch-cards/keys [cards error-message page page-size count-of-cards]}]
  (if error-message
    (assoc state
           kws.cards-grid/error-message error-message
           kws.cards-grid/status kws.cards-grid/status-error)
    (assoc state
           kws.cards-grid/error-message nil
           kws.cards-grid/status kws.cards-grid/status-ready
           kws.cards-grid/cards cards
           kws.cards-grid/page page
           kws.cards-grid/page-size page-size
           kws.cards-grid/count-of-cards count-of-cards)))

(defn- fetch-cards-params
  "Maps the state to the params for fetching cards"
  [{::kws.cards-grid/keys [page page-size include-tags exclude-tags tags-filter-query]}]
  (cond-> {kws.fetch-cards/page page
           kws.fetch-cards/page-size page-size
           kws.fetch-cards/include-tags include-tags
           kws.fetch-cards/exclude-tags exclude-tags}
    tags-filter-query (assoc kws.fetch-cards/tags-filter-query tags-filter-query)))

(defn- refetch!
  "Refetches the data from the BE"
  [state fetch-cards!]
  (a/go
    (log "Fetching cards...")
    (swap! state reduce-on-fetched-cards (-> @state fetch-cards-params fetch-cards! a/<!))))

(defn- has-previous-page?
  "Returns true if we can go to a previous page given the current app state."
  [{::kws.cards-grid/keys [page]}]
  (> page 1))

(defn- has-next-page?
  "Returns true if we can go to a next page given the current app state."
  [{::kws.cards-grid/keys [page count-of-cards page-size]}]
  (< page (utils.pagination/last-page page-size count-of-cards)))

(defn- set-config-profile
  "Reducer that set's the config given a `profile`"
  [state
   {{::kws.config/keys [page page-size include-tags exclude-tags tags-filter-query]}
    kws.profile/config}]
  (assoc state
         kws.cards-grid/page page
         kws.cards-grid/page-size page-size
         kws.cards-grid/include-tags include-tags
         kws.cards-grid/exclude-tags exclude-tags
         kws.cards-grid/tags-filter-query tags-filter-query))

;; API
(def init-state! refetch!)

(defn initialize-from-props!
  "Initializes the component's state."
  [{:keys [state] ::kws.cards-grid/keys [fetch-cards!]}]
  (log "Initilizing state...")
  (when (not (state-initialized? @state))
    (init-state! state fetch-cards!)))

(defn refetch-from-props!
  "Refetches the cards data using the props."
  [{:keys [state] ::kws.cards-grid/keys [fetch-cards!]}]
  (log "Refetching data...")
  (refetch! state fetch-cards!))

(defn set-page-from-props!
  "Set's page on the state"
  [{:keys [state] ::kws.cards-grid/keys [fetch-cards!]} new-page]
  (swap! state assoc kws.cards-grid/page new-page)
  (refetch! state fetch-cards!))

(defn set-page-size-from-props!
  "Set's page size on the state"
  [{:keys [state] ::kws.cards-grid/keys [fetch-cards!]} new-page-size]
  (swap! state assoc kws.cards-grid/page-size new-page-size)
  (refetch! state fetch-cards!))

(defn set-include-tags-from-props!
  "Set's the tags the cards must include from props"
  [{:keys [state] ::kws.cards-grid/keys [fetch-cards!]} new-include-tags]
  (swap! state assoc kws.cards-grid/include-tags (tags/sanitize new-include-tags))
  (refetch! state fetch-cards!))

(defn set-exclude-tags-from-props!
  "Set's the tags the cards must not have (exclude-tags) from props"
  [{:keys [state] ::kws.cards-grid/keys [fetch-cards!]} new-exclude-tags]
  (swap! state assoc kws.cards-grid/exclude-tags (tags/sanitize new-exclude-tags))
  (refetch! state fetch-cards!))

(defn set-tags-filter-query-from-props!
  "Set's the value for the tags filter query."
  [{:keys [state] ::kws.cards-grid/keys [fetch-cards!]} new-tags-filter-query]
  (swap! state assoc kws.cards-grid/tags-filter-query new-tags-filter-query)
  (refetch! state fetch-cards!))

(defn set-config-from-loader!
  "Set's the entire config from the response of `services.cards-grid-profile-manager`"
  [{:keys [state] ::kws.cards-grid/keys [fetch-cards!]}
   {::kws.cards-grid-profile-manager/keys [fetched-profile success?] :as loader-response}]
  (log "Setting config from loader response:" loader-response)
  (if success?
    (do
      (swap! state set-config-profile fetched-profile)
      (refetch! state fetch-cards!))
    (log "NOT SET BECAUSE FAILED RESPONSE")))

(defn goto-previous-page!
  "Navigates to the previous page."
  [{:keys [state] :as opts}]
  (when (has-previous-page? @state)
    (set-page-from-props! opts (-> @state kws.cards-grid/page dec))))

(defn goto-next-page!
  "Navigates to the next page."
  [{:keys [state] :as opts}]
  (when (has-next-page? @state)
    (set-page-from-props! opts (-> @state kws.cards-grid/page inc))))
