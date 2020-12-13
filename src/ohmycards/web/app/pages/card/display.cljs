(ns ohmycards.web.app.pages.card.display
  (:require [ohmycards.web.kws.services.routing.core :as kws.routing]
            [ohmycards.web.kws.services.routing.pages :as routing.pages]
            [ohmycards.web.kws.views.display-card.core :as kws.display-card]
            [ohmycards.web.services.login.core :as services.login]
            [ohmycards.web.views.display-card.core :as display-card]
            [ohmycards.web.views.display-card.handlers :as display-card.handlers]))

(defonce ^:private ^:dynamic *props* nil)

(defn view       [] [display-card/main *props*])
(defn hydra-head [] (display-card.handlers/hydra-head *props*))

(defn init!
  [{:keys [state fetch-card! goto-home! goto-editcard! fetch-card-history! to-clipboard!
           storage-peek!]}]
  (set! *props* {:state state
                 kws.display-card/fetch-card! fetch-card!
                 kws.display-card/goto-home! goto-home!
                 kws.display-card/goto-editcard! goto-editcard!
                 kws.display-card/fetch-card-history! fetch-card-history!
                 kws.display-card/to-clipboard! to-clipboard!
                 kws.display-card/storage-peek! storage-peek!}))

(defn- enter-hook
  "Enter hook for display-card, performing initialization logic."
  [route-match]
  (when (services.login/is-logged-in?)
    (let [card-id (some-> route-match :parameters :query :id)
          storage-key (some-> route-match :parameters :query :storage-key)]
      (display-card.handlers/init! *props* card-id storage-key))))

(def ^:private update-hook enter-hook)

(def route
  ["/display"
   {kws.routing/name routing.pages/display-card
    kws.routing/view #'view
    kws.routing/enter-hook enter-hook
    kws.routing/update-hook update-hook}])
