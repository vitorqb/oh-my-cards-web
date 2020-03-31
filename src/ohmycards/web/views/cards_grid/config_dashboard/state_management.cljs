(ns ohmycards.web.views.cards-grid.config-dashboard.state-management
  (:require [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [ohmycards.web.views.cards-grid.config-dashboard.config-parser :as config-parser]))

(defn reset-config
  "Reducer. Resets the state to have a specific configuration."
  [{::kws/keys [config] :as state} new-config]
  (let [parsed-config (->> new-config
                           (map (fn [[k v]] [k (config-parser/to-coercion-result k v)]))
                           (into {}))]
    (assoc state kws/config parsed-config)))
