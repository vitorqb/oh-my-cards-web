(ns ohmycards.web.services.routing.core
  (:require [ohmycards.web.kws.services.routing.core :as kws]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.services.logging.core :as logging]
            [reitit.core :as r]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]))

(logging/deflogger log "Routing.Core")

;;
;; State
;;
(defonce ^:private ^:dynamic *state* nil)
(defonce ^:private ^:dynamic *router* nil)
(defonce ^:private ^:dynamic *opts* nil)

;;
;; Helpers
;;
(defn- run-hook! [h m]
  (when-let [hook (some-> m :data h)]
    (log (str "Running hook " h " for match ") m)
    (hook m)))

(defn- do-route!
  "Performs the routing logic, as expected by `rfe/start!`
  This first argument must be the routing state, and the second the new match."
  [routing-state match]
  (log "Routing..." {:routing-state routing-state :match match})
  (let [old-match @routing-state]
    (reset! routing-state match)
    (events-bus/send! kws/action-navigated-to-route [old-match match])))

(defn- new-query-params
  "Calculates the new query params given the current match, service
  options and the params to update"
  [current-match opts new-params-map]
  (let [current (:query-params current-match)
        global (kws/global-query-params opts)]
    (merge (select-keys current global) new-params-map)))

;;
;; Public API
;; 
(defn goto!
  "Navigates to a route."
  [k & {::kws/keys [query-params]}]
  (log "Navigating to" k query-params)
  (rfe/push-state k {} (new-query-params @*state* *opts* query-params)))

(defn run-view-hooks!
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

(defn update-query-params!
  "Updates the query parameters, without changing the route.
  Each key value pair in `m` will be merged to the query params."
  [m]
  (let [current-match @*state*
        current-route-name (-> current-match :data kws/name)
        current-query-params (:query-params current-match)]
    (goto! current-route-name kws/query-params (merge current-query-params m))))

(defn start-routing!
  "Starts the routing with reitit.
  - `raw-routes` Must be a reitit-like route registration data.
  - `routing-state` An atom-like thing where to store state is stored.
  Available `opts` are:
  - `global-query-params` A set of global query-params that are kept between all routings."
  ([raw-routes routing-state] (start-routing! raw-routes routing-state nil))
  ([raw-routes routing-state opts]
   (log "Starting with" raw-routes routing-state)
   (set! *state* routing-state)
   (set! *router* (rf/router raw-routes))
   (set! *opts* opts)
   (rfe/start!
    *router*
    (fn [match _] (do-route! *state* match))
    {:use-fragment true})))

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
  
  ([view-name {:keys [query-params path-params]} router]
   (let [match (r/match-by-name router view-name path-params)
         path (r/match->path match (new-query-params @*state* *opts* query-params))
         path-with-hash (str "/#" path)]
     path-with-hash)))
