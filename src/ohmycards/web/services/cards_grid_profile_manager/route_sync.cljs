(ns ohmycards.web.services.cards-grid-profile-manager.route-sync
  "Module responsible for synchronizing the profile with the current route params."
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.services.cards-grid-profile-manager.core :as core]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.services.routing.core :as services.routing]
            [ohmycards.web.services.logging.core :as logging]))

(logging/deflogger log "Services.CardsGridProfileLoader.RouteSync")

(defonce ^:private current-profile (atom nil))

;; Helpers
(defn- load!
  "Returns a channel with a fetch profile response for a given name."
  [profile-name]
  (if (nil? profile-name)
    (a/go {kws/success? true kws/fetched-profile nil})
    (core/load! profile-name)))

(defn- load-from-route-change!
  "Loads a profile from a change on the url."
  [old-match new-match]
  (let [new-profile-name (some-> new-match :query-params :grid-profile)
        old-profile-name (some-> old-match :query-params :grid-profile)]
    (when (not= new-profile-name old-profile-name)
      (load! new-profile-name))))

(defn- handle-success!
  "Handles a new profile successfully fetched."
  [response]
  (let [new-profile (kws/fetched-profile response)]
    (reset! current-profile new-profile)
    (events-bus/send! kws/action-new-grid-profile new-profile)))

(defn- handle-error!
  "Handles a failed new profile fetch."
  [response]
  (reset! current-profile nil)
  (events-bus/send! kws/action-new-grid-profile nil))

(defn- handle-response!
  "Handles a fetch response"
  [response]
  (if (kws/success? response)
    (handle-success! response)
    (handle-error! response)))

;; API
(defn react-to-route-change!
  "Reacts to a route change by refetching the profile and sending a msg
  through the bus if needed."
  [old-match new-match]
  (when-let [response-chan (load-from-route-change! old-match new-match)]
    (log "Loading from route change...")
    (a/go (handle-response! (a/<! response-chan)))))

(defn load-from-route!
  "Loads a profile from a route match"
  [match]
  (log "Loading from route...")
  (a/go (-> match :query-params :grid-profile load! a/<! handle-response!)))

(defn set-in-route!
  "Set's a profile name in the route, causing a loading."
  [profile-name]
  (services.routing/update-query-params! {:grid-profile profile-name}))
