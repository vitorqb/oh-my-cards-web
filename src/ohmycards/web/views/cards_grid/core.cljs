(ns ohmycards.web.views.cards-grid.core
  (:require [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.markdown-displayer.core
             :as
             markdown-displayer]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.views.cards-grid.core :as kws]
            [ohmycards.web.views.cards-grid.control-filter :as control-filter]
            [ohmycards.web.views.cards-grid.control-header :as control-header]
            [ohmycards.web.views.cards-grid.state-management :as state-management]))

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
   [:div.card-display__body
    [markdown-displayer/main {:source body}]]
   [tags-displayer {::tags tags}]
   [:div.card-display__foot [card-edit-btn props]]])

(defn- main*
  "Rendering logic for the `main` component."
  [{:keys [state] :as props}]
  [:div.cards-grid
   [control-header/main props]
   (when (kws/filter-enabled? @state)
     [control-filter/main props])
   [:div.cards-grid__top-error-box
    [error-message-box/main {:value (kws/error-message @state)}]]
   (if-not (empty? (kws/cards @state))
     (for [card (kws/cards @state)]
       ^{:key (kws.card/id card)}
       [card-display (assoc props ::card card)])
     [:div.cards-grid__empty-msgbox
      [:div "Nothing to display. Create your first card to get started!"]])])

(defn main
  "A view for a grid of cards."
  [{:keys [state] :as props}]
  (state-management/initialize-from-props! props)
  (fn [props] (main* props)))
