(ns ohmycards.web.views.display-card.child.card-history-displayer
  "Controls the `card-history-displayer` component that is used by the `display-card` page."
  (:require [ohmycards.web.components.card-history-displayer.core
             :as
             components.card-history-displayer]
            [ohmycards.web.kws.components.card-history-displayer.core
             :as
             kws.card-history-displayer]
            [ohmycards.web.kws.views.display-card.core :as kws]
            [reagent.core :as r]))

(defn get-props
  "Get's props for `card-history-displayer` from `display-card` props."
  [{:keys [state] ::kws/keys [fetch-card-history!]}]
  {:state (r/cursor state [::card-history-displayer])
   kws.card-history-displayer/fetch-card-history! fetch-card-history!})

(defn fetch-history-async-action
  "Returns an async action to fetch the history."
  [props card-id]
  (components.card-history-displayer/fetch-history-async-action (get-props props) card-id))

(defn main
  "Wrapper around `ohmycards.web.components.card-history-displayer` that accepts the props
  for `display-card`."
  [props]
  [components.card-history-displayer/main (get-props props)])
