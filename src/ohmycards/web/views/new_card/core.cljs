(ns ohmycards.web.views.new-card.core
  (:require [ohmycards.web.views.new-card.header :as header]
            [ohmycards.web.views.new-card.form :as form]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.good-message-box.core :as good-message-box]
            [ohmycards.web.kws.card :as kws.card]))

(defn- success-message [x] (and x (str "Created card with uuid " (kws.card/id x))))

(defn main
  "A view to add a new card."
  [{:keys [state] :as props}]
  [:div.new-card
   [loading-wrapper/main {:loading? (kws/loading? @state)}
    [header/main props]
    [error-message-box/main {:value (kws/error-message @state)}]
    [good-message-box/main {:value (-> @state kws/created-card success-message)}]
    [form/main props]]])
