(ns ohmycards.web.controllers.cards-grid.core
  "The cards grid controller is a small system that glues together the
  `cards-grid` view, the `cards-grid.config-dashboard` views and the
  `cards-grid-profile-manager` service. Those three actors are unware of
  each other, but the entire app depends on a nice sync for them. This
  sync happens on this controller."
  (:require [cljs.core.async :as async]
            [ohmycards.web.app.provider :as app.provider]
            [ohmycards.web.app.state :as app.state]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.cards-grid.metadata.core :as kws.cards-grid.metadata]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.hydra.leaf :as kws.hydra.leaf]
            [ohmycards.web.kws.lenses.metadata :as lenses.metadata]
            [ohmycards.web.kws.services.routing.core :as kws.routing]
            [ohmycards.web.kws.services.routing.pages :as routing.pages]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core
             :as
             kws.config-dashboard]
            [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [ohmycards.web.services.cards-grid-profile-manager.core
             :as
             services.cards-grid-profile-manager]
            [ohmycards.web.services.cards-grid-profile-manager.route-sync
             :as
             services.cards-grid-profile-manager.route-sync]
            [ohmycards.web.services.fetch-cards.core :as services.fetch-cards]
            [ohmycards.web.services.routing.core :as services.routing]
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
  (services.routing/goto! routing.pages/cards-grid-config))

(defn- route-to-new-card! []
  (services.routing/goto! routing.pages/new-card))

(defn- route-to-grid! []
  (services.routing/goto! routing.pages/home))

(defn- route-to-profiles! []
  (services.routing/goto! routing.pages/profiles))


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
(defn- load-profile! [profile-name]
  (services.cards-grid-profile-manager.route-sync/set-in-route! profile-name))

(defn- save-profile!
  "Uses the profile manager to save a profile in the BE."
  [profile]
  (services.cards-grid-profile-manager/save! profile))

;; Props generator
(defn- grid-props
  "Returns the props for the grid view instance."
  []
  {:state                        *grid-state*
   :path-to!                     services.routing/path-to!
   :to-clipboard!                app.provider/to-clipboard!
   kws.cards-grid/goto-settings! route-to-config-dashboard!
   kws.cards-grid/goto-newcard!  route-to-new-card!
   kws.cards-grid/goto-profiles! route-to-profiles!
   kws.cards-grid/fetch-cards!   app.provider/fetch-cards!})

(defn- config-dashboard-props
  "Returns the props for the config dashboard."
  []
  {:state                                      *config-dashboard-state*
   kws.config-dashboard/goto-cards-grid!       route-to-grid!
   kws.config-dashboard/set-page!              set-grid-page!
   kws.config-dashboard/set-page-size!         set-grid-page-size!
   kws.config-dashboard/set-include-tags!      set-grid-include-tags!
   kws.config-dashboard/set-exclude-tags!      set-grid-exclude-tags!
   kws.config-dashboard/set-tags-filter-query! set-grid-tags-filter-query!
   kws.config-dashboard/load-profile!          load-profile!
   kws.config-dashboard/save-profile!          save-profile!
   kws.config-dashboard/profiles-names         (-> @app.state/state
                                                   lenses.metadata/cards-grid
                                                   kws.cards-grid.metadata/profile-names)
   kws.config-dashboard/cards-metadata         (-> @app.state/state lenses.metadata/cards)})

;; 
;; Public API
;;
(defn init!
  "Initializes the controller"
  []
  (log "Initializing...")
  (init-grid-state! app.state/state)
  (init-config-dashboard-state! app.state/state))

(defn cards-grid
  "An instance of the cards grid view."
  []
  [cards-grid/main (grid-props)])

(defn config-dashboard-page
  "An instance of the cards grid config dashboard view."
  [props]
  [config-dashboard/main (config-dashboard-props)])

(defn refetch!
  "Refetches the cards for the grid."
  []
  (cards-grid.state-management/refetch! (grid-props)))

(defn new-grid-profile!
  "Reacts to a new grid profile being selected."
  [new-profile]
  (cards-grid.state-management/set-profile! (grid-props) new-profile)
  (config-dashboard.state-management/set-profile! (config-dashboard-props) new-profile))

(def routes
  "All routes controlled by this controller."
  [["/"
    {kws.routing/name routing.pages/home
     kws.routing/view #'cards-grid}]
   ["/cards-grid/config"
    {kws.routing/name routing.pages/cards-grid-config
     kws.routing/view #'config-dashboard-page}]])

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
