(ns ohmycards.web.views.cards-grid.control-header
  (:require [ohmycards.web.utils.pagination :as utils.pagination]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.views.cards-grid.core :as kws]
            [ohmycards.web.utils.components :as utils.components]
            [ohmycards.web.views.cards-grid.state-management :as state-management]))

(defn- arrow-left [props]
  [:button.clear-button {:on-click #(state-management/goto-previous-page! props)}
   [icons/arrow-left]])

(defn- arrow-right [props]
  [:button.clear-button {:on-click #(state-management/goto-next-page! props)}
   [icons/arrow-right]])

(defn- page-counter [{::keys [page max-page]}]
  [:span.small-box (str page " | " max-page)])

(defn- settings-btn [goto-settings!]
  [:button.clear-button {:on-click #(goto-settings!)}
   [icons/settings]])

(defn- header-left []
  [:span.cards-grid__header-left])

(defn- header-center
  "The center of the header, containing the pagination controls."
  [{:keys [state] :as props}]
  (let [{::kws/keys [page page-size count-of-cards goto-previous-page!]} @state]
    [:span.cards-grid__header-center
     [arrow-left props]
     [page-counter {::page page ::max-page (utils.pagination/last-page page-size count-of-cards)}]
     [arrow-right props]]))

(defn- header-right [{::kws/keys [goto-settings!]}]
  [:span.cards-grid__header-right
   [settings-btn goto-settings!]])

(defn main
  "A header for the grid with controls."
  [{:keys [state] ::kws/keys [goto-settings! goto-previous-page!] :as props}]
  [:div.cards-grid__control-header
   [header-left]
   [header-center props]
   [header-right props]])
