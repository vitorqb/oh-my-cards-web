(ns ohmycards.web.views.cards-grid.control-filter
  (:require [ohmycards.web.components.form.core :as form]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [ohmycards.web.views.cards-grid.state-management :as state-management]))

(defn main
  "The control for filtering base on a search term."
  [{:keys [state] :as props}]
  (let [value     (kws.cards-grid/filter-input-search-term @state)
        on-change #(swap! state assoc kws.cards-grid/filter-input-search-term %)
        on-submit #(do (.preventDefault %) (state-management/commit-search! props))]
    [:div.cards-grid-control-filter
     [:span.label "Search:"]
     [:form.cards-grid-control-filter__form {:on-submit on-submit}
      [:input.cards-grid-control-filter__input
       {:auto-focus true
        :value      value
        :on-change  #(-> % .-target .-value on-change)}]
      [:button.icon-button.cards-grid-control-filter__submit-btn [icons/check]]]]))

