(ns ohmycards.web.views.new-card.form
  (:require [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.inputs.markdown :as inputs.markdown]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.components.inputs.tags :as kws.inputs.tags]
            [ohmycards.web.kws.views.new-card.core :as kws]))

(defn- title-input
  "An input for the title"
  [{:keys [state]}]
  [form/row {}
   [:span.new-card-form__label "Title"]
   [form.input/main (form.input/build-props state [kws/card-input kws.card/title]
                                            :auto-focus true)]])

(defn- body-input
  "An input for the body"
  [{:keys [state]}]
  [form/row {}
   [:span.new-card-form__label "Body"] 
   [inputs.markdown/main (form.input/build-props state [kws/card-input kws.card/body])]])

(defn- tags-input
  "An input for tags"
  [{:keys [state] ::kws/keys [cards-metadata]}]
  [form/row {}
   [:span.new-card-form__label "Tags"]
   [:div.simple-form__input
    [inputs.tags/main (assoc (form.input/build-props state [kws/card-input kws.card/tags])
                             kws.inputs.tags/all-tags (kws.card-metadata/tags cards-metadata))]]])

(defn main
  "A form for creating a new card."
  [props]
  [:div.new-card-form
   [form/main {}
    [title-input props]
    [body-input props]
    [tags-input props]]])
