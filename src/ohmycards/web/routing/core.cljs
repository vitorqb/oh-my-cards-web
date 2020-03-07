(ns ohmycards.web.routing.core
  (:require [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]))

(defn goto!
  "Navigates to a route."
  [k]
  (rfe/push-state k))

(defn start-routing!
  "Starts the routing with reitit.
  - `raw-routes` Must be a reitit-like route registration data.
  - `set-match!` A fn to set the routing match on change."
  [raw-routes set-match!]
  (rfe/start!
   (rf/router raw-routes)
   (fn [match _] (set-match! match))
   {:use-fragment true}))
