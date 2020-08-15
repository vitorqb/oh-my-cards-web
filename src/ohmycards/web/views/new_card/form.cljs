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
  (let [path        [kws/card-input kws.card/title]
        input-props (-> (form.input/build-props state path)
                        (assoc :auto-focus true))
        input       [form.input/main input-props]]
    [form/row {:label "Title" :input input}]))

(defn- body-input
  "An input for the body"
  [{:keys [state]}]
  (let [path        [kws/card-input kws.card/body]
        input-props (form.input/build-props state path)
        input       [inputs.markdown/main input-props]]
    [form/row {:label "Body" :input input}]))

(defn- tags-input
  "An input for tags"
  [{:keys [state] ::kws/keys [cards-metadata]}]
  (let [all-tags    (kws.card-metadata/tags cards-metadata)
        path        [kws/card-input kws.card/tags]
        input-props (-> (form.input/build-props state path)
                        (assoc kws.inputs.tags/all-tags all-tags))
        input       [inputs.tags/main input-props]]
    [form/row {:label "Tags" :input input}]))

(defn main
  "A form for creating a new card."
  [props]
  [:div.new-card-form
   [form/main {}
    [title-input props]
    [body-input props]
    [tags-input props]]])
