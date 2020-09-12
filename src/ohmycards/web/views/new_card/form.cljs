(ns ohmycards.web.views.new-card.form
  (:require [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.components.inputs.markdown :as inputs.markdown]
            [ohmycards.web.components.inputs.simple :as inputs.simple]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.components.inputs.tags :as kws.inputs.tags]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [reagent.core :as r]))

(defn- title-input
  "An input for the title"
  [{:keys [state]}]
  [form/row
   {:label "Title"
    :input [inputs/main
            {kws.inputs/cursor (r/cursor state [kws/card-input kws.card/title])
             kws.inputs/props {:auto-focus true}}]}])

(defn- body-input
  "An input for the body"
  [{:keys [state]}]
  [form/row
   {:label "Body"
    :input [inputs/main
            {kws.inputs/itype kws.inputs/t-markdown
             kws.inputs/cursor (r/cursor state [kws/card-input kws.card/body])}]}])

(defn- tags-input
  "An input for tags"
  [{:keys [state] ::kws/keys [cards-metadata]}]
  (let [all-tags (kws.card-metadata/tags cards-metadata)]
    [form/row
     {:label "Tags"
      :input [inputs/main
              {kws.inputs/itype kws.inputs/t-tags
               kws.inputs/cursor (r/cursor state [kws/card-input kws.card/tags])
               kws.inputs/props {kws.inputs.tags/all-tags all-tags}}]}]))

(defn main
  "A form for creating a new card."
  [props]
  [:div.new-card-form
   [form/main {}
    [title-input props]
    [body-input props]
    [tags-input props]]])
