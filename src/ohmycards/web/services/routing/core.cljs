(ns ohmycards.web.services.routing.core
  (:require [ohmycards.web.kws.services.routing.core :as kws]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.utils.logging :as logging]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]))

(logging/deflogger log "Routing.Core")

(defn goto!
  "Navigates to a route."
  [k & {::kws/keys [query-params]}]
  (log "Navigating to" k query-params)
  (rfe/push-state k {} query-params))

(defn start-routing!
  "Starts the routing with reitit.
  - `raw-routes` Must be a reitit-like route registration data.
  - `routing-state` An atom-like thing where to store state is stored."
  [raw-routes routing-state]
  (log "Starting with" raw-routes routing-state)
  (rfe/start!
   (rf/router raw-routes)
   (fn [match _]
     (reset! routing-state match)
     (events-bus/send! kws/action-navigated-to-route match))
   {:use-fragment true}))
