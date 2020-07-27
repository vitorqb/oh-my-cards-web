(ns ohmycards.web.components.markdown-displayer.core
  (:require ["react-markdown" :as ReactMarkdown]
            [reagent.core :as r]))

(defn- update-with-max-width
  "Updates the props image by parsing any `max-width` attribute set in `alt`.
  We support adding the `max-width` style attribute by identifying a
  syntax similar to ` MAX_WIDTH=100px` in the `:alt` attribute."
  [props]
  (if-let [[_ group1 group2] (re-find #"^(.*) MAX_WIDTH=([^ ]+)$" (:alt props))]
    (-> props
        (assoc :alt group1)
        (assoc-in [:style :max-width] group2))
    props))

(defn- image-renderer
  "Renders an image for the markdown component. Enriches it to support MAX_WIDTH."
  [props]
  (r/as-element [:img.mde-preview__image (update-with-max-width props)]))

(def ^:private renderers {"image" #(image-renderer (js->clj % :keywordize-keys true))})

(defn main
  "Renders a component to display markdown."
  [{:keys [source]}]
  [:div.mde-preview
   [:div.mde-preview-content
    [:> ReactMarkdown {:source source :renderers renderers}]]])
