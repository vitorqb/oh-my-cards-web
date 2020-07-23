(ns ohmycards.web.views.new-card.header
  (:require [ohmycards.web.components.header.core :as header]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.views.new-card.handlers.create-card :as create-card]))

(defn header-left
  [{::kws/keys [goto-home!]}]
  [:button.icon-button {:on-click #(goto-home!)}
   [icons/home]])

(defn header-center
  [props]
  [:button.icon-button.u-color-good {:on-click #(create-card/main props)}
   [icons/check]])

(defn main
  "The header of the new card view."
  [props]
  [header/main {:left   [header-left props]
                :center [header-center props]}])
