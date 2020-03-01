(ns ohmycards.web.components.loading-wrapper.core)

(defn- loading-view
  []
  [:div.loading-wrapper
   [:span.loading-wrapper__label
    "Loading..."]])

(defn main
  "Wraps all children in a loading state view."
  [{:keys [loading?]} & children]
  (let [children* (map-indexed #(with-meta %2 {:key %1}) children)]
    (if-not loading?
      [:<> children*]
      [:<> [loading-view] children*])))
