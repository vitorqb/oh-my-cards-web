(ns ohmycards.web.views.cards-grid.config-dashboard.core
  (:require [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [ohmycards.web.components.form.core :as form]))

(defn- label [x] [:span.cards-grid-config-dashboard__label x])
(defn- input [state kw] [form/input {:class "cards-grid-config-dashboard__input"
                                     :type "number"
                                     :on-change #(swap! state assoc kw %)
                                     :value (kw @state)}])
(defn- set-btn [f] [:button.cards-grid-config-dashboard__set {:on-click #(f)} "Set"])

(defn- header
  "A header with options"
  [{::kws/keys [goto-cards-grid!]}]
  [:div.cards-grid-config-dashboard__header
   [:button.clear-button {:on-click #(goto-cards-grid!)}
    [icons/arrow-left]]])

(defn- page-config
  "A config input for a page"
  [{:keys [state] ::kws/keys [set-page!]}]
  [:div.cards-grid-config-dashboard__row
   (label "Page")
   (input state kws/page)
   (set-btn #(set-page! (kws/page @state)))])

(defn- page-size-config
  "A config input for page size"
  [{:keys [state] ::kws/keys [set-page-size!]}]
  [:div.cards-grid-config-dashboard__row
   (label "Page Size")
   (input state kws/page-size)
   (set-btn #(set-page-size! (kws/page-size @state)))])

(defn main
  "A configuration dashboard for the cards grid."
  [props]
  [:div.cards-grid-config-dashboard
   [header props]
   [page-config props]
   [page-size-config props]])
