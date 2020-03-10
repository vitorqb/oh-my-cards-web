(ns ohmycards.web.views.new-card.form
  (:require [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.kws.card :as kws.card]))

(defn- title-input
  "An input for the title"
  [{:keys [state]}]
  [form/row {}
   [:span.label "Title"]
   [form/input {:type "text"
                :value (-> @state kws/card-input kws.card/title)
                :on-change #(swap! state assoc-in [kws/card-input kws.card/title] %)}]])

(defn- body-input
  "An input for the body"
  [{:keys [state]}]
  [form/row {}
   [:span.label "Body"]
   [form/input {:type "text"
                :value (-> @state kws/card-input kws.card/body)
                :on-change #(swap! state assoc-in [kws/card-input kws.card/body] %)}]])

(defn main
  "A form for creating a new card."
  [props]
  [:div.new-card-form
   [form/main {}
    [title-input props]
    [body-input props]]])
