(ns ohmycards.web.components.card-history-displayer.field-update-displayer
  (:require [ohmycards.web.kws.common.cards.history.core :as kws.cards.history]
            [ohmycards.web.kws.components.card-history-displayer.field-update-displayer
             :as
             kws]))

(defn js->string [x] (if (string? x) x (js/JSON.stringify x nil 2)))

(defn main
  "Displays a field update for a card update event."
  [{::kws/keys [field-update] :as props}]
  [:div.field-update-displayer
   [:h4 "Updated " (kws.cards.history/field-name field-update)]
   [:div.field-update-displayer__comparison
    [:pre.field-update-displayer__old-value
     (-> field-update kws.cards.history/old-value clj->js js->string)]
    [:pre.field-update-displayer__new-value
     (-> field-update kws.cards.history/new-value clj->js js->string)]]])
