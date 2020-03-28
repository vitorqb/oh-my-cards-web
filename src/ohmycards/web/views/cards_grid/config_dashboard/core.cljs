(ns ohmycards.web.views.cards-grid.config-dashboard.core
  (:require [ohmycards.web.common.tags.core :as tags]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]))

;; Helpers
(defn- label [x] [:span.cards-grid-config-dashboard__label x])

(defn- set-btn [f]
  [:div.cards-grid-config-dashboard__set-wrapper
   [:button.cards-grid-config-dashboard__set {:on-click #(f)} "Set"]])

(defn- input-wrapper
  [_ & children]
  (into [:div.cards-grid-config-dashboard__input-wrapper] children))

(defn input-props
  [state path]
  (form.input/build-props state path :class "cards-grid-config-dashboard__input"))

(defn- header
  "A header with options"
  [{::kws/keys [goto-cards-grid!]}]
  [:div.cards-grid-config-dashboard__header
   [:button.clear-button {:on-click #(goto-cards-grid!)}
    [icons/arrow-left]]])

;; Inputs
(defn- page-config
  "A config input for a page"
  [{:keys [state] ::kws/keys [set-page!]}]
  [:div.cards-grid-config-dashboard__row
   (label "Page")
   [input-wrapper {}
    [form.input/main (input-props state [kws/page])]]
   (set-btn #(set-page! (kws/page @state)))])

(defn- page-size-config
  "A config input for page size"
  [{:keys [state] ::kws/keys [set-page-size!]}]
  [:div.cards-grid-config-dashboard__row
   (label "Page Size")
   [input-wrapper {}
    [form.input/main (input-props state [kws/page-size])]]
   (set-btn #(set-page-size! (kws/page-size @state)))])

(defn- include-tags-config
  "A config for tags to include"
  [{:keys [state] ::kws/keys [set-include-tags!]}]
  [:div.cards-grid-config-dashboard__row
   (label "ALL tags")
   [input-wrapper {}
    [inputs.tags/main (input-props state [kws/include-tags])]]
   (set-btn #(let [tags (->> @state kws/include-tags tags/sanitize vec)]
               (swap! state assoc kws/include-tags tags)
               (set-include-tags! tags)))])

(defn- exclude-tags-config
  "A config for tags to exclude"
  [{:keys [state] ::kws/keys [set-exclude-tags!]}]
  [:div.cards-grid-config-dashboard__row
   (label "Not ANY tags")
   [input-wrapper {}
    [inputs.tags/main (input-props state [kws/exclude-tags])]]
   (set-btn #(let [tags (->> @state kws/exclude-tags tags/sanitize vec)]
               (swap! state assoc kws/exclude-tags tags)
               (set-exclude-tags! tags)))])

;; Main
(defn main
  "A configuration dashboard for the cards grid."
  [props]
  [:div.cards-grid-config-dashboard
   [header props]
   [page-config props]
   [page-size-config props]
   [include-tags-config props]
   [exclude-tags-config props]])
