(ns ohmycards.web.views.cards-grid.config-dashboard.state-management
  (:require [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core
             :as
             kws.cards-grid-profile-manager]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [ohmycards.web.utils.logging :as logging]
            [ohmycards.web.views.cards-grid.config-dashboard.config-parser
             :as
             config-parser]))

(logging/deflogger log "Views.CardsGrid.ConfigDashboard.StateManagement")

(defn reset-config
  "Reducer. Resets the state to have a specific configuration."
  [{::kws/keys [config] :as state} new-config]
  (let [parsed-config (->> new-config
                           (map (fn [[k v]] [k (config-parser/to-coercion-result k v)]))
                           (into {}))]
    (assoc state kws/config parsed-config)))

(defn set-config-from-loader!
  "Set's a config from a `services.cards-grid-config-loader` fetch response."
  [state {::kws.cards-grid-profile-manager/keys [success? fetched-profile] :as fetched}]
  (log "Resetting config from loader:" fetched)
  (if success?
    (swap! state reset-config (kws.profile/config fetched-profile))
    (log "FAILED: fetch was not successfull")))

(defn init-state
  "Reducer that initializes the state for the config dashboard."
  [state]
  (assoc state kws/save-profile-name (coercion.result/failure "" coercers/not-min-length)))
