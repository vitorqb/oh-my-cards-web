(ns ohmycards.web.views.new-card.core
  (:require [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.good-message-box.core :as good-message-box]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.leaf :as kws.hydra.leaf]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.views.new-card.form :as form]
            [ohmycards.web.views.new-card.handlers.create-card
             :as
             handlers.create-card]
            [ohmycards.web.views.new-card.header :as header]))

(defn- success-message [x] (and x (str "Created card with uuid " (kws.card/id x))))

(defn main
  "A view to add a new card."
  [{:keys [state] :as props}]
  [:div.new-card
   [loading-wrapper/main {:loading? (kws/loading? @state)}
    [header/main props]
    [:div.u-center
     [error-message-box/main {:value (kws/error-message @state)}]
     [good-message-box/main {:value (-> @state kws/created-card success-message)}]]
    [form/main props]]])

(defn hydra-head
  "Returns hydra heads for the contextual menu."
  [props]
  {kws.hydra/type         kws.hydra/branch
   kws.hydra.branch/name  "Create Card Hydra"
   kws.hydra.branch/heads [{kws.hydra/shortcut    \c
                            kws.hydra/description "Create"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(handlers.create-card/main props)}
                           {kws.hydra/shortcut    \q
                            kws.hydra/description "Quit"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(do)}]})
