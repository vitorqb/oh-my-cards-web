(ns ohmycards.web.views.new-card.header
  (:require [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.views.new-card.handlers.create-card :as create-card]))

(defn header-left
  [{::kws/keys [goto-home!]}]
  [:div.new-card-header__left
   [:button.clear-button {:on-click #(goto-home!)}
    [icons/home]]])

(defn header-center
  [props]
  [:div.new-card-header__center
   [:button.clear-button.u-color-good {:on-click #(create-card/main props)}
    [icons/check]]])

(defn main
  "The header of the new card view."
  [props]
  [:div.new-card-header
   [header-left props]
   [header-center props]
   [:div.new-card-header__right]])
