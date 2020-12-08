(ns ohmycards.web.views.cards-grid.state-management
  (:require [ohmycards.web.common.async-actions.core :as async-actions]
            [ohmycards.web.common.tags.core :as tags]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.services.fetch-cards.core :as kws.fetch-cards]
            [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [ohmycards.web.services.logging.core :as logging]
            [ohmycards.web.utils.pagination :as utils.pagination]))

(logging/deflogger log "Views.CardsGrid.StateManagement")

;; Helpers
(defn- fetch-cards-async-action [{:keys [state] ::kws.cards-grid/keys [fetch-cards!]}]
  {kws.async-actions/state
   state

   kws.async-actions/pre-reducer-fn
   #(assoc % kws.cards-grid/status kws.cards-grid/status-loading)

   kws.async-actions/post-reducer-fn
   (fn [s {::kws.fetch-cards/keys [cards error-message page page-size count-of-cards]}]
     (if error-message
       (assoc s
              kws.cards-grid/error-message error-message
              kws.cards-grid/status kws.cards-grid/status-error)
       (-> s
           (assoc kws.cards-grid/error-message nil
                  kws.cards-grid/status kws.cards-grid/status-ready
                  kws.cards-grid/cards cards
                  kws.cards-grid/count-of-cards count-of-cards)
           (assoc-in [kws.cards-grid/config kws.config/page] page)
           (assoc-in [kws.cards-grid/config kws.config/page-size] page-size))))

   kws.async-actions/action-fn
   (fn [s]
     (fetch-cards! {kws.fetch-cards/config (kws.cards-grid/config s)
                    kws.fetch-cards/search-term (kws.cards-grid/search-term s)}))})

(defn- refetch!
  "Refetches the data from the BE"
  [props]
  (async-actions/run (fetch-cards-async-action props)))

(defn- has-previous-page?
  "Returns true if we can go to a previous page given the current app state."
  [{{page kws.config/page} kws.cards-grid/config}]
  (> page 1))

(defn- has-next-page?
  "Returns true if we can go to a next page given the current app state."
  [{count-of-cards kws.cards-grid/count-of-cards
    {page kws.config/page page-size kws.config/page-size} kws.cards-grid/config}]
  (< page (utils.pagination/last-page page-size count-of-cards)))

(defn- set-config-profile
  "Reducer that set's the config given a `profile`"
  [state {config kws.profile/config}]
  (assoc state kws.cards-grid/config config))

;; API
(defn toggle-filter!
  [{:keys [state] :as props}]
  (log "Activating filter...")
  (swap! state update kws.cards-grid/filter-enabled? not)
  (when-not (kws.cards-grid/filter-enabled? @state)
    (swap! state assoc kws.cards-grid/search-term "")
    (refetch! props)))

(defn commit-search!
  [{:keys [state] :as props}]
  (let [new-search-term (kws.cards-grid/filter-input-search-term @state)
        old-search-term (some-> @state kws.cards-grid/search-term)]
    (when-not (= new-search-term old-search-term)
      (log (str "Committing search to " new-search-term "..."))
      (swap! state #(-> %
                        (assoc kws.cards-grid/search-term new-search-term)
                        (assoc-in [kws.cards-grid/config kws.config/page] 1)))
      (refetch! props))))

(defn set-page-from-props!
  "Set's page on the state"
  [{:keys [state] :as props} new-page]
  (swap! state assoc-in [kws.cards-grid/config kws.config/page] new-page)
  (refetch! props))

(defn set-page-size-from-props!
  "Set's page size on the state"
  [{:keys [state] :as props} new-page-size]
  (swap! state assoc-in [kws.cards-grid/config kws.config/page-size] new-page-size)
  (refetch! props))

(defn set-include-tags-from-props!
  "Set's the tags the cards must include from props"
  [{:keys [state] :as props} new-include-tags]
  (swap! state
         assoc-in
         [kws.cards-grid/config kws.config/include-tags]
         (tags/sanitize new-include-tags))
  (refetch! props))

(defn set-exclude-tags-from-props!
  "Set's the tags the cards must not have (exclude-tags) from props"
  [{:keys [state] :as props} new-exclude-tags]
  (swap! state assoc-in [kws.cards-grid/config kws.config/exclude-tags] (tags/sanitize new-exclude-tags))
  (refetch! props))

(defn set-tags-filter-query-from-props!
  "Set's the value for the tags filter query."
  [{:keys [state] :as props} new-tags-filter-query]
  (swap! state assoc-in [kws.cards-grid/config kws.config/tags-filter-query] new-tags-filter-query)
  (refetch! props))

(defn set-profile!
  "Set's a new grid profile."
  [{:keys [state] :as props} new-profile]
  (swap! state set-config-profile new-profile)
  (refetch! props))

(defn goto-previous-page!
  "Navigates to the previous page."
  [{:keys [state] :as opts}]
  (when (has-previous-page? @state)
    (set-page-from-props! opts (-> @state kws.cards-grid/config kws.config/page dec))))

(defn goto-next-page!
  "Navigates to the next page."
  [{:keys [state] :as opts}]
  (when (has-next-page? @state)
    (set-page-from-props! opts (-> @state kws.cards-grid/config kws.config/page inc))))

(defn loading?
  "Returns logical true if we are loading."
  [{:keys [state]}]
  (or (= (kws.cards-grid/status @state) kws.cards-grid/status-loading)
      (nil? (kws.cards-grid/status @state))))
