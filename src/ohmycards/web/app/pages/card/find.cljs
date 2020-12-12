(ns ohmycards.web.app.pages.card.find
  (:require [ohmycards.web.kws.services.routing.core :as kws.routing]
            [ohmycards.web.kws.services.routing.pages :as routing.pages]
            [ohmycards.web.views.find-card.core :as views.find-card]))

(defonce ^:dynamic ^:private *props* nil)

(defn view []
  [views.find-card/main *props*])

(def route
  ["/find"
   {kws.routing/name routing.pages/find-card
    kws.routing/view #'view}])

(defn init!
  "Initializes the page"
  [{:keys [state run-http-action]}]
  (set! *props* {:state state
                 :run-http-action run-http-action})
  (views.find-card/init-state! *props*))
