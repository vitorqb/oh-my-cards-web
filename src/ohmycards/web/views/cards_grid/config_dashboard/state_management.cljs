(ns ohmycards.web.views.cards-grid.config-dashboard.state-management
  (:require [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core
             :as
             kws.cards-grid-profile-manager]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [ohmycards.web.services.logging.core :as logging]
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

(defn set-profile!
  "Set's a new grid profile."
  [props new-profile]
  (swap! (:state props) reset-config (kws.profile/config new-profile)))

(defn init-state
  "Reducer that initializes the state for the config dashboard."
  [state]
  (assoc state kws/save-profile-name (coercion.result/failure "" coercers/not-min-length)))
