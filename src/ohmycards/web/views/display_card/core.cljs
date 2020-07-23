(ns ohmycards.web.views.display-card.core
  (:require [ohmycards.web.components.header.core :as header]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.components.markdown-displayer.core
             :as
             markdown-displayer]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.views.display-card.core :as kws]
            [ohmycards.web.views.display-card.handlers :as handlers]))

;; 
;; Private components
;;
 (defn- title [{:keys [state]}]
   [:div.display-card__title (-> @state kws/card kws.card/title)])

(defn- id [{:keys [state]}]
  [:div.display-card__id (-> @state kws/card kws.card/id)])

(defn- body [{:keys [state]}]
  [:div.display-card__body
   [markdown-displayer/main {:source (-> @state kws/card kws.card/body)}]])

(defn- extra-info [{:keys [state]}]
  [:div.display-card__extra-info-box
   [:span.display-card__extra-info-label.display-card__extra-info-label--left
    (str "created " (-> @state kws/card kws.card/created-at))]
   [:span.display-card__extra-info-label.display-card__extra-info-label--right
    (str "updated " (-> @state kws/card kws.card/updated-at))]])

(defn- footer [{:keys [state]}]
  [:div.display-card__footer
   (for [tag (-> @state kws/card kws.card/tags)]
     ^{:key tag} [:span.display-card__tag tag])])

(defn- header [{::kws/keys [goto-home!]}]
  [header/main {:left [:button.clear-button {:on-click #(goto-home!)}
                       [icons/arrow-left]]}])

;;
;; API
;; 
(defn main
  "A view component that displays a card."
  [{:keys [state] :as props}]
  [:div.display-card
   [loading-wrapper/main {:loading? (kws/loading? @state)}
    [header props]
    [:div.display-card__content
     [:div.display-card__header
      [id props]
      [extra-info props]
      [title props]]
     [body props]
     [footer props]]]])
