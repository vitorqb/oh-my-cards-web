(ns ohmycards.web.views.cards-grid.control-header
  (:require [ohmycards.web.components.header.core :as header]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.views.cards-grid.core :as kws]
            [ohmycards.web.utils.components :as utils.components]
            [ohmycards.web.utils.pagination :as utils.pagination]
            [ohmycards.web.views.cards-grid.state-management :as state-management]))

(defn- arrow-left [props]
  [:button.icon-button {:on-click #(state-management/goto-previous-page! props)}
   [icons/arrow-left]])

(defn- arrow-right [props]
  [:button.icon-button {:on-click #(state-management/goto-next-page! props)}
   [icons/arrow-right]])

(defn- page-counter [{::keys [page max-page]}]
  [:span.small-box (str page " | " max-page)])

(defn- settings-btn [goto-settings!]
  [:button.icon-button {:on-click #(goto-settings!)}
   [icons/settings]])

(defn- profiles-btn [{::kws/keys [goto-profiles!]}]
  [:button.icon-button {:on-click #(goto-profiles!)}
   [icons/profile]])

(defn- refresh-btn [props]
  [:button.icon-button {:on-click #(state-management/refetch! props)}
   [icons/refresh]])

(defn- filter-btn [props]
  [:button.icon-button {:on-click #(state-management/toggle-filter! props)}
   [icons/filter]])

(defn- new-card-btn [goto-newcard!]
  [:button.icon-button.u-color-good {:on-click #(goto-newcard!)}
   [icons/add]])

(defn- header-left [props]
  [:<>
   [new-card-btn (kws/goto-newcard! props)]
   [refresh-btn props]
   [filter-btn props]])

(defn- header-center
  "The center of the header, containing the pagination controls."
  [{:keys [state] ::kws/keys [] :as props}]
  (let [{config kws/config count-of-cards kws/count-of-cards} @state
        {page kws.config/page page-size kws.config/page-size} config]
    [:<>
     [arrow-left props]
     [page-counter {::page page ::max-page (utils.pagination/last-page page-size count-of-cards)}]
     [arrow-right props]]))

(defn- header-right [{::kws/keys [goto-settings! goto-profiles!]}]
  [:<>
   [settings-btn goto-settings!]
   [profiles-btn {kws/goto-profiles! goto-profiles!}]])

(defn main
  "A header for the grid with controls."
  [props]
  [header/main {:left [header-left props]
                :center [header-center props]
                :right [header-right props]}])
