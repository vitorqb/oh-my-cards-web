(ns ohmycards.web.app.pages.card.edit
  (:require [ohmycards.web.kws.services.routing.core :as kws.routing]
            [ohmycards.web.kws.services.routing.pages :as routing.pages]
            [ohmycards.web.kws.views.edit-card.core :as kws.edit-card]
            [ohmycards.web.services.login.core :as services.login]
            [ohmycards.web.views.edit-card.core :as edit-card]
            [ohmycards.web.views.edit-card.handlers :as edit-card.handlers]
            [ohmycards.web.views.edit-card.state-management
             :as
             edit-card.state-management]))

(defonce ^:private ^:dynamic *props* nil)

(defn view       [] [edit-card/main *props*])
(defn hydra-head [] (edit-card.handlers/hydra-head *props*))

(defn init!
  [{:keys [state goto-home! goto-displaycard! fetch-card! update-card! cards-metadata
           confirm-deletion-fn! delete-card! notify]}]
  (set! *props* {:state state
                 :notify! notify
                 kws.edit-card/goto-home! goto-home!
                 kws.edit-card/goto-displaycard! goto-displaycard!
                 kws.edit-card/fetch-card! fetch-card!
                 kws.edit-card/update-card! update-card!
                 kws.edit-card/cards-metadata cards-metadata
                 kws.edit-card/confirm-deletion-fn! confirm-deletion-fn!
                 kws.edit-card/delete-card! delete-card!}))

(defn enter-hook! [route-match]
  (when (services.login/is-logged-in?)
    (edit-card.state-management/init-from-route-match! *props* route-match)))

(defn update-hook! [_ route-match]
  (enter-hook! route-match))

(def route
  ["/edit"
   {kws.routing/name routing.pages/edit-card
    kws.routing/view view
    kws.routing/enter-hook enter-hook!
    kws.routing/update-hook update-hook!}])
