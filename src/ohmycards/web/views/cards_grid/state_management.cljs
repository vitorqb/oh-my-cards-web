(ns ohmycards.web.views.cards-grid.state-management
  (:require [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [cljs.core.async :as a]
            [ohmycards.web.kws.services.fetch-cards.core :as kws.fetch-cards]
            [ohmycards.web.utils.pagination :as utils.pagination]))

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
  [{::kws.cards-grid/keys [page page-size]}]
  {::kws.fetch-cards/page page ::kws.fetch-cards/page-size page-size})

(defn- refetch!
  "Refetches the data from the BE"
  [state fetch-cards!]
  (a/go
    (js/console.log "Fetching cards...")
    (swap! state reduce-on-fetched-cards (-> @state fetch-cards-params fetch-cards! a/<!))))

(defn- has-previous-page?
  "Returns true if we can go to a previous page given the current app state."
  [{::kws.cards-grid/keys [page]}]
  (> page 1))

(defn- has-next-page?
  "Returns true if we can go to a next page given the current app state."
  [{::kws.cards-grid/keys [page count-of-cards page-size]}]
  (< page (utils.pagination/last-page page-size count-of-cards)))

(def init-state! refetch!)

(defn initialize-from-props!
  "Initializes the component's state."
  [{:keys [state] ::kws.cards-grid/keys [fetch-cards!]}]
  (js/console.log "Initilizing state for cards-grid...")
  (when (not (state-initialized? @state))
    (init-state! state fetch-cards!)))

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
