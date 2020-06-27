(ns ohmycards.web.kws.services.routing.core
  (:refer-clojure :exclude [name]))

;; Routing data
(def name
  "The name (keyword) of the routing we are navigating into.
  This is a normal keyword because that's what reitit expects."
  :name)
(def view "The view component (Ragent) of the routing we are navigating into" ::view)

;; Params and Options
(def query-params "Query parameters for routing." ::query-params)

;; Bus Events/Actions
(def action-navigated-to-route "An action representing that the app navigated to a route."
  ::action-navigated-to-route)
