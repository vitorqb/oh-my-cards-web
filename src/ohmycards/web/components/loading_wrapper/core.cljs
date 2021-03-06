(ns ohmycards.web.components.loading-wrapper.core
  (:require [ohmycards.web.components.spinner.core :as spinner]
            [ohmycards.web.utils.components :as utils.components]))

(defn- loading-view
  []
  [:div.loading-wrapper
   [:div.loading-wrapper__spinner-box
    [spinner/main]]])

(defn main
  "Wraps all children in a loading state view."
  [{:keys [loading?]} & children]
  (if-not loading?
    [:<> (utils.components/with-seq-keys children)]
    [:<> [loading-view] (utils.components/with-seq-keys children)]))
