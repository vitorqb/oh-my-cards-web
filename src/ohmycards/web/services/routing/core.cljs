(ns ohmycards.web.services.routing.core
  (:require [ohmycards.web.kws.services.routing.core :as kws]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.utils.logging :as logging]
            [reitit.core :as r]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]))

(logging/deflogger log "Routing.Core")

;;
;; State
;;
(defonce ^:private ^:dynamic *state* nil)
(defonce ^:private ^:dynamic *router* nil)

;;
;; Helpers
;;
(defn- run-hook! [h m]
  (when-let [hook (some-> m :data h)]
    (log (str "Running hook " h " for match ") m)
    (hook m)))

(defn- run-view-hooks!
  "Runs view hooks for `enter`, `update` or `leave`.
  Those are functions that are called to perform any initialization/cleanup logic the views
  may need."
  [old-match new-match]
  (if (= (some-> old-match :data :name) (some-> new-match :data :name))
    (do
      (run-hook! kws/update-hook new-match))
    (do
      (run-hook! kws/exit-hook old-match)
      (run-hook! kws/enter-hook new-match))))

(defn- do-route!
  "Performs the routing logic, as expected by `rfe/start!`
  This first argument must be the routing state, and the second the new match."
  [routing-state match]
  (log "Routing stated!" {:routing-state routing-state :match match})
  (let [old-match @routing-state]
    (run-view-hooks! old-match match))
  (reset! routing-state match)
  (events-bus/send! kws/action-navigated-to-route match))

;;
;; Public API
;; 
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
  (set! *state* routing-state)
  (set! *router* (rf/router raw-routes))
  (rfe/start!
   *router*
   (fn [match _] (do-route! *state* match))
   {:use-fragment true}))

(defn force-update!
  "Forcely runs the `update` hook for the current match. This can be useful, for example,
  after a login actions."
  []
  (run-hook! kws/update-hook @*state*))

(defn path-to!
  "Returns the link to a given view name."
  ([view-name]
   (path-to! view-name nil *router*))

  ([view-name opts]
   (path-to! view-name opts *router*))
  
  ([view-name opts router]
   (let [match (r/match-by-name router view-name (:path-params opts))
         path (r/match->path match (:query-params opts))
         path-with-hash (str "/#" path)]
     path-with-hash)))
