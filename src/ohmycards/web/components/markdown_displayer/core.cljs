(ns ohmycards.web.components.markdown-displayer.core
  (:require ["react-markdown" :as ReactMarkdown]
            [reagent.core :as r]))

(defn main
  "Renders a component to display markdown."
  [{:keys [source]}]
  [:div.mde-preview
   [:div.mde-preview-content
    [:> ReactMarkdown {:source source}]]])
