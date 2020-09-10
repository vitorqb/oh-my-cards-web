(ns ohmycards.web.controllers.cards-grid.core
  "The cards grid controller is a small system that glues together the
  `cards-grid` view, the `cards-grid.config-dashboard` views and the
  `cards-grid-profile-manager` service. Those three actors are unware of
  each other, but the entire app depends on a nice sync for them. This
  sync happens on this controller."
  (:require [cljs.core.async :as async]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.hydra.leaf :as kws.hydra.leaf]
            [ohmycards.web.kws.services.routing.core :as kws.routing]
            [ohmycards.web.kws.services.routing.pages :as routing.pages]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core
             :as
             kws.config-dashboard]
            [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [ohmycards.web.services.cards-grid-profile-manager.core
             :as
             services.cards-grid-profile-manager]
            [ohmycards.web.services.routing.core :as routing.core]
            [ohmycards.web.services.fetch-cards.core :as services.fetch-cards]
            [ohmycards.web.services.routing.core :as services.routing]
            [ohmycards.web.app.provider :as app.provider]
            [ohmycards.web.utils.logging :as logging]
            [ohmycards.web.views.cards-grid.config-dashboard.core
             :as
             config-dashboard]
            [ohmycards.web.views.cards-grid.config-dashboard.state-management
             :as
             config-dashboard.state-management]
            [ohmycards.web.views.cards-grid.core :as cards-grid]
            [ohmycards.web.views.cards-grid.state-management
             :as
             cards-grid.state-management]
            [reagent.core :as r]))

;;
;; Constants
;;

(logging/deflogger log "Controllers.CardsGrid")

;; The state for the cards grid
(defonce ^:private ^:dynamic *grid-state* nil)

;; The state for the config dashboard
(defonce ^:private ^:dynamic *config-dashboard-state* nil)

;; The http-fn to use
(defonce ^:private ^:dynamic *http-fn* nil)

;; The options for using the profile manager svc
(defonce ^:private ^:dynamic *cards-grid-profile-manager-opts* nil)

;; 
;; Helper Fns
;;

;; Initializing...
(defn- init-grid-state!
  "Initializes the grid state from the app state."
  [app-state]
  (set! *grid-state* (r/cursor app-state [:views.cards-grid])))

(defn- init-config-dashboard-state!
  "Initializes the configuration dashboard state from the app state."
  [app-state]
  (let [new-state (r/cursor app-state [:views.cards-grid.config-dashboard])]
    (swap! new-state config-dashboard.state-management/init-state)
    (set! *config-dashboard-state* new-state)))

;; Routing...
(defn- route-to-config-dashboard! []
  (routing.core/goto! routing.pages/cards-grid-config))

(defn- route-to-new-card! []
  (routing.core/goto! routing.pages/new-card))

(defn- route-to-grid! []
  (routing.core/goto! routing.pages/home))



;; Config setting
(declare grid-props)

(defn- set-grid-page! [x]
  (cards-grid.state-management/set-page-from-props! (grid-props) x))

(defn- set-grid-page-size! [x]
  (cards-grid.state-management/set-page-size-from-props! (grid-props) x))

(defn- set-grid-include-tags! [x]
  (cards-grid.state-management/set-include-tags-from-props! (grid-props) x))

(defn- set-grid-exclude-tags! [x]
  (cards-grid.state-management/set-exclude-tags-from-props! (grid-props) x))

(defn- set-grid-tags-filter-query! [x]
  (cards-grid.state-management/set-tags-filter-query-from-props! (grid-props) x))

(defn- goto-next-page! []
  (cards-grid.state-management/goto-next-page! (grid-props)))

(defn- goto-previous-page! []
  (cards-grid.state-management/goto-previous-page! (grid-props)))

(defn- toggle-filter! []
  (cards-grid.state-management/toggle-filter! (grid-props)))

;; Other svcs
(defn- fetch-cards!
  "Fetches the cards for the grid."
  [opts]
  (services.fetch-cards/main (assoc opts :http-fn *http-fn*)))

(defn- load-profile!
  "Uses the profile manager to load a profile for the cards grid and
  updates both the grid and the config with it."
  [profile-name]
  (let [chan (services.cards-grid-profile-manager/load! {:http-fn *http-fn*} profile-name)]
    (async/go
      (let [resp (async/<! chan)]
        (config-dashboard.state-management/set-config-from-loader! *config-dashboard-state* resp)
        (cards-grid.state-management/set-config-from-loader! (grid-props) resp)))))

(defn- save-profile!
  "Uses the profile manager to save a profile in the BE."
  [profile]
  (services.cards-grid-profile-manager/save! *cards-grid-profile-manager-opts* profile))

;; Props generator
(defn- grid-props
  "Returns the props for the grid view instance."
  []
  {:state                        *grid-state*
   :path-to!                     services.routing/path-to!
   :to-clipboard!                app.provider/to-clipboard!
   kws.cards-grid/goto-settings! route-to-config-dashboard!
   kws.cards-grid/goto-newcard!  route-to-new-card!
   kws.cards-grid/fetch-cards!   fetch-cards!})

(defn- config-dashboard-props
  "Returns the props for the config dashboard."
  []
  {:state *config-dashboard-state*
   kws.config-dashboard/goto-cards-grid!       route-to-grid!
   kws.config-dashboard/set-page!              set-grid-page!
   kws.config-dashboard/set-page-size!         set-grid-page-size!
   kws.config-dashboard/set-include-tags!      set-grid-include-tags!
   kws.config-dashboard/set-exclude-tags!      set-grid-exclude-tags!
   kws.config-dashboard/set-tags-filter-query! set-grid-tags-filter-query!
   kws.config-dashboard/load-profile!          load-profile!
   kws.config-dashboard/save-profile!          save-profile!})

;; 
;; Public API
;; 
(defn init!
  "Initializes the controller"
  [{:keys [app-state http-fn cards-grid-profile-manager-opts]}]
  (log "Initializing...")
  (init-grid-state! app-state)
  (init-config-dashboard-state! app-state)
  (set! *http-fn* http-fn)
  (set! *cards-grid-profile-manager-opts* cards-grid-profile-manager-opts))

(defn cards-grid
  "An instance of the cards grid view."
  []
  [cards-grid/main (grid-props)])

(defn config-dashboard-page
  "An instance of the cards grid config dashboard view."
  [props]
  (let [props' (merge (config-dashboard-props) props)]
    [config-dashboard/main props']))

(defn refetch!
  "Refetches the cards for the grid."
  []
  (cards-grid.state-management/refetch-from-props! (grid-props)))

(defn load-profile-from-route-match!
  "Given a route match (reitit), loads the profile for the cards grid if it is defined."
  [route-match]
  (when-let [profile-name (some-> route-match :query-params :grid-profile)]
    (log "Loading grid-profile in the route: " profile-name)
    (let [view (-> route-match :data kws.routing/name)
          query-params (-> route-match :query-params (dissoc :grid-profile))]
      (load-profile! profile-name)
      (routing.core/goto! view kws.routing/query-params query-params))))

(defn hydra-head
  "Returns a head for contextual hydra, with actions the user can make on the current grid."
  []
  {kws.hydra/type         kws.hydra/branch
   kws.hydra.branch/name  "Grid Actions"
   kws.hydra.branch/heads [{kws.hydra/shortcut    \r
                            kws.hydra/description "Refresh"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(refetch!)}
                           {kws.hydra/shortcut    \n
                            kws.hydra/description "Next Page"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(goto-next-page!)}
                           {kws.hydra/shortcut    \p
                            kws.hydra/description "Previous Page"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(goto-previous-page!)}
                           {kws.hydra/shortcut    \f
                            kws.hydra/description "Toggle Filter"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(toggle-filter!)}
                           {kws.hydra/shortcut    \q
                            kws.hydra/description "Quit"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(do)}]})
