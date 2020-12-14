(ns ohmycards.web.app.pages.card.find
  (:require [ohmycards.web.kws.services.routing.core :as kws.routing]
            [ohmycards.web.kws.services.routing.pages :as routing.pages]
            [ohmycards.web.kws.views.find-card.core :as kws.find-card]
            [ohmycards.web.views.find-card.core :as views.find-card]))

(defonce ^:dynamic ^:private *props* nil)

(defn view []
  [views.find-card/main *props*])

(defn init!
  "Initializes the page"
  [{:keys [state run-http-action goto-displaycard! fetch-card! storage-put! goto-home!]}]
  (set! *props* {:state state
                 :run-http-action run-http-action
                 kws.find-card/goto-displaycard! goto-displaycard!
                 kws.find-card/fetch-card! fetch-card!
                 kws.find-card/storage-put! storage-put!
                 kws.find-card/goto-home! goto-home!})
  (views.find-card/init-state! *props*))

(def route
  ["/find"
   {kws.routing/name routing.pages/find-card
    kws.routing/view #'view
    kws.routing/enter-hook #(views.find-card/refresh! *props*)}])
