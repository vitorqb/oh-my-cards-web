(ns ohmycards.web.views.edit-card.core
  (:require [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.good-message-box.core :as good-message-box]
            [ohmycards.web.components.header.core :as header]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.components.inputs.markdown :as inputs.markdown]
            [ohmycards.web.components.inputs.simple :as inputs.simple]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.components.inputs.tags :as kws.inputs.tags]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.views.edit-card.handlers :as handlers]
            [reagent.core :as r]))

;; Components
(defn- go-home-btn
  [{::kws/keys [goto-home!]}]
  [:button.icon-button {:on-click #(goto-home!)} [icons/home]])

(defn- remove-btn
  [props]
  [:button.icon-button.u-color-bad {:on-click #(handlers/delete-card! props)}
   [icons/trash]])

(defn- update-btn
  [props]
  [:button.icon-button.u-color-good {:on-click #(handlers/update-card! props)}
   [icons/check]])

(defn- display-btn
  [props]
  [:button.icon-button.u-color-good {:on-click #(handlers/goto-displaycard! props)}
   [icons/view]])

(defn- header
  "The header of the edit card page."
  [props]
  [header/main {:left   [go-home-btn props]
                :center [:<> [remove-btn props]
                             [update-btn props]
                             [display-btn props]]}])

(defn- id-input-row [{:keys [state]}]
  [form/row
   {:label "Id"
    :input [inputs/main
            {kws.inputs/cursor (r/cursor state [kws/card-input kws.card/id])
             kws.inputs/disabled? true}]}])

(defn- ref-input-row [{:keys [state]}]
  [form/row
   {:label "Ref"
    :input [inputs/main
            {kws.inputs/cursor (r/cursor state [kws/card-input kws.card/ref])
             kws.inputs/disabled? true}]}])

(defn- title-input-row
  [{:keys [state]}]
  [form/row
   {:label "Title"
    :input [inputs/main
            {kws.inputs/cursor (r/cursor state [kws/card-input kws.card/title])}]}])

(defn- body-input-row
  [{:keys [state]}]
  [form/row
   {:label "Body"
    :input [inputs/main
            {kws.inputs/itype kws.inputs/t-markdown
             kws.inputs/cursor (r/cursor state [kws/card-input kws.card/body])}]}])

(defn- tags-input-row
  "An input for tags"
  [{:keys [state] ::kws/keys [cards-metadata]}]
  (let [all-tags (kws.card-metadata/tags cards-metadata)]
    [form/row
     {:label "Tags"
      :input [inputs/main
              {kws.inputs/itype kws.inputs/t-tags
               kws.inputs/cursor (r/cursor state [kws/card-input kws.card/tags])
               kws.inputs/props {kws.inputs.tags/all-tags all-tags}}]}]))

(defn- form
  "The form for the card inputs."
  [props]
  [form/main {}
   [id-input-row props]
   [ref-input-row props]
   [title-input-row props]
   [body-input-row props]
   [tags-input-row props]])

(defn main
  "Main view to edit an existing card."
  [{:keys [state] :as props}]
  [:div.edit-card
   [loading-wrapper/main {:loading? (kws/loading? @state)}
    [header props]
    [:div.u-center
     [error-message-box/main {:value (kws/error-message @state)}]
     [good-message-box/main {:value (kws/good-message @state)}]]
    [form props]]])
