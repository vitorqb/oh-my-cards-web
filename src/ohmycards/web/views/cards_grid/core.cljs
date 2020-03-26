(ns ohmycards.web.views.cards-grid.core
  (:require [ohmycards.web.kws.views.cards-grid.core :as kws]
            [ohmycards.web.views.cards-grid.state-management :as state-management]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.views.cards-grid.control-header :as control-header]
            [ohmycards.web.icons :as icons]))

;; Functions
(defn- card-edit-btn
  "A button to edit a card."
  [{::keys [card] ::kws/keys [goto-editcard!]}]
  [:button.clear-button.u-color-good {:on-click #(goto-editcard! card)}
   [icons/edit]])

(defn- tags-displayer
  "A component to display cards in a single line."
  [{::keys [tags]}]
  [:div.tags-displayer {}
   (for [tag tags]
     ^{:key tag} [:span.tags-displayer__tag tag])])

(defn- card-display
  "A component to display a single card."
  [{{::kws.card/keys [id title body tags] :as card} ::card :as props}]
  [:div.card-display
   [:div.card-display__title title]
   [:div.card-display__body body]
   [tags-displayer {::tags tags}]
   [:div.card-display__foot [card-edit-btn props]]])

(defn- main*
  "Rendering logic for the `main` component."
  [{:keys [state] :as props}]
  [:div.cards-grid
   [control-header/main props]
   [:div.cards-grid__top-error-box
    [error-message-box/main {:value (kws/error-message @state)}]]
   (for [card (kws/cards @state)]
     ^{:key (kws.card/id card)}
     [card-display (assoc props ::card card)])])

(defn main
  "A view for a grid of cards."
  [{:keys [state] :as props}]
  (state-management/initialize-from-props! props)
  (fn [props] (main* props)))
