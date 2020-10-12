(ns ohmycards.web.components.card-history-displayer.field-update-displayer
  (:require [ohmycards.web.kws.common.cards.history.core :as kws.cards.history]
            [ohmycards.web.kws.components.card-history-displayer.field-update-displayer
             :as
             kws]))

(defn main
  "Displays a field update for a card update event."
  [{::kws/keys [field-update] :as props}]
  [:div.field-update-displayer
   [:h4 "Updated " (kws.cards.history/field-name field-update)]
   [:div.field-update-displayer__comparison
    [:pre.field-update-displayer__old-value
     (with-out-str (cljs.pprint/pprint (kws.cards.history/old-value field-update)))]
    [:pre.field-update-displayer__new-value
     (with-out-str (cljs.pprint/pprint (kws.cards.history/new-value field-update)))]]])
