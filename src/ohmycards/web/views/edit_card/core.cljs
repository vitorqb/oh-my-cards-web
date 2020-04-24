(ns ohmycards.web.views.edit-card.core
  (:require [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.good-message-box.core :as good-message-box]
            [ohmycards.web.components.inputs.markdown :as inputs.markdown]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.views.edit-card.handlers :as handlers]))

;; Components
(defn- go-home-btn
  [{::kws/keys [goto-home!]}]
  [:button.edit-card__button {:on-click #(goto-home!)} [icons/home]])

(defn- remove-btn
  [props]
  [:button.edit-card__button.edit-card__button--bad {:on-click #(handlers/delete-card! props)}
   [icons/trash]])

(defn- update-btn
  [props]
  [:button.edit-card__button.edit-card__button--good {:on-click #(handlers/update-card! props)}
   [icons/check]])

(defn- header
  "The header of the edit card page."
  [props]
  [:div.edit-card-header
   [:div.edit-card-header__left [go-home-btn props]]
   [:div.edit-card-header__center
    [remove-btn props]
    [update-btn props]]
   [:div.edit-card-header__right]])

(defn- id-input-row
  [{:keys [state]}]
  [form/row {}
   [:span.edit-card__label "Id"]
   [form.input/main (form.input/build-props state
                                            [kws/card-input kws.card/id]
                                            :disabled true)]])

(defn- title-input-row
  [{:keys [state]}]
  [form/row {}
   [:span.edit-card__label "Title"]
   [form.input/main (form.input/build-props state [kws/card-input kws.card/title])]])

(defn- body-input-row
  [{:keys [state]}]
  [form/row {}
   [:span.edit-card__label "Body"]
   [inputs.markdown/main (form.input/build-props state [kws/card-input kws.card/body])] ])

(defn- tags-input-row
  "An input for tags"
  [{:keys [state]}]
  [form/row {}
   [:span.edit-card__label "Tags"]
   [:div.simple-form__input
    [inputs.tags/main (form.input/build-props state [kws/card-input kws.card/tags])]]])

(defn- form
  "The form for the card inputs."
  [props]
  [form/main {}
   [id-input-row props]
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
