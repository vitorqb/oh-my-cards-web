(ns ohmycards.web.routing.core
  (:require [ohmycards.web.kws.routing.core :as kws]
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
  - `set-match!` A fn to set the routing match on change."
  [raw-routes set-match!]
  (log "Starting with" raw-routes set-match!)
  (rfe/start!
   (rf/router raw-routes)
   (fn [match _] (set-match! match))
   {:use-fragment true}))
