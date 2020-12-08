(ns ohmycards.web.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.app.logging :as app.logging]
            [ohmycards.web.app.provider :as app.provider]
            [ohmycards.web.app.state :as app.state]
            [ohmycards.web.common.utils :as utils]
            [ohmycards.web.components.app-header.core :as header]
            [ohmycards.web.components.current-view.core :as components.current-view]
            [ohmycards.web.controllers.action-dispatcher.core
             :as
             controllers.action-dispatcher]
            [ohmycards.web.controllers.cards-grid.core :as controllers.cards-grid]
            [ohmycards.web.controllers.clipboard-dialog.core
             :as
             controllers.clipboard-dialog]
            [ohmycards.web.controllers.file-upload-dialog.core
             :as
             controllers.file-upload-dialog]
            [ohmycards.web.globals :as globals]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.cards-grid.metadata.core :as kws.cards-grid.metadata]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.hydra.leaf :as kws.hydra.leaf]
            [ohmycards.web.kws.lenses.login :as lenses.login]
            [ohmycards.web.kws.lenses.metadata :as lenses.metadata]
            [ohmycards.web.kws.lenses.routing :as lenses.routing]
            [ohmycards.web.kws.services.cards-crud.actions
             :as
             kws.cards-crud.actions]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core
             :as
             kws.services.cards-grid-profile-manager]
            [ohmycards.web.kws.services.events-bus.core :as kws.events-bus]
            [ohmycards.web.kws.services.login.core :as kws.services.login]
            [ohmycards.web.kws.services.routing.core :as kws.routing]
            [ohmycards.web.kws.services.routing.pages :as routing.pages]
            [ohmycards.web.kws.services.shortcuts-register.core
             :as
             kws.services.shortcuts-register]
            [ohmycards.web.kws.user :as kws.user]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core
             :as
             kws.cards-grid.config-dashboard]
            [ohmycards.web.kws.views.display-card.core :as kws.display-card]
            [ohmycards.web.kws.views.edit-card.core :as kws.edit-card]
            [ohmycards.web.kws.views.new-card.core :as kws.new-card]
            [ohmycards.web.kws.views.profiles.core :as kws.views.profiles]
            [ohmycards.web.services.cards-grid-profile-manager.core
             :as
             services.cards-grid-profile-manager]
            [ohmycards.web.services.cards-grid-profile-manager.route-sync
             :as
             services.cards-grid-profile-manager.route-sync]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.services.logging.core :as services.logging]
            [ohmycards.web.services.login.core :as services.login]
            [ohmycards.web.services.notify :as services.notify]
            [ohmycards.web.services.routing.core :as services.routing]
            [ohmycards.web.services.shortcuts-register.core
             :as
             services.shortcuts-register]
            [ohmycards.web.services.user-question.core :as services.user-question]
            [ohmycards.web.views.about.core :as views.about]
            [ohmycards.web.views.cards-grid.config-dashboard.core
             :as
             cards-grid.config-dashboard]
            [ohmycards.web.views.display-card.core :as display-card]
            [ohmycards.web.views.display-card.handlers :as display-card.handlers]
            [ohmycards.web.views.edit-card.core :as edit-card]
            [ohmycards.web.views.edit-card.handlers :as edit-card.handlers]
            [ohmycards.web.views.edit-card.state-management
             :as
             edit-card.state-management]
            [ohmycards.web.views.login.core :as views.login]
            [ohmycards.web.views.new-card.core :as new-card]
            [ohmycards.web.views.profiles.core :as views.profiles]
            [reagent.dom :as r.dom]))

;; -------------------------
;; View instances

;; Edit card
(defn edit-card-page-props
  "Props for the `edit-card-page`."
  []
  {kws.edit-card/goto-home! #(services.routing/goto! routing.pages/home)
   kws.edit-card/goto-displaycard! #(services.routing/goto! routing.pages/display-card
                                                            kws.routing/query-params {:id %})
   kws.edit-card/fetch-card! app.provider/fetch-card!
   kws.edit-card/update-card! app.provider/update-card!
   kws.edit-card/cards-metadata (lenses.metadata/cards @app.state/state)
   kws.edit-card/confirm-deletion-fn! services.user-question/confirm-card-delete
   kws.edit-card/delete-card! app.provider/delete-card!
   :state (app.state/state-cursor :views.edit-card)
   :notify! app.provider/notify!})

(defn edit-card-page
  "An instance for the edit-card view"
  []
  [edit-card/main (edit-card-page-props)])

(defn edit-card-enter-hook!
  "Enter hook for edit-card, setting state on entering."
  [route-match]
  (when (services.login/is-logged-in?)
    (edit-card.state-management/init-from-route-match! (edit-card-page-props) route-match)))

(def edit-card-update-hook! edit-card-enter-hook!)

;; Display card
(defn display-card-page-props []
  {:state                          (app.state/state-cursor :views.display-card)
   kws.display-card/fetch-card!    app.provider/fetch-card!
   kws.display-card/goto-home!     #(services.routing/goto! routing.pages/home)
   kws.display-card/goto-editcard! #(services.routing/goto! routing.pages/edit-card
                                                            kws.routing/query-params {:id %})
   kws.display-card/fetch-card-history! app.provider/fetch-card-history!
   kws.display-card/to-clipboard! app.provider/to-clipboard!})

(defn display-card-page []
  [display-card/main (display-card-page-props)])

(defn display-card-enter-hook
  "Enter hook for display-card, performing initialization logic."
  [route-match]
  (when (services.login/is-logged-in?)
    (display-card.handlers/init! (display-card-page-props)
                                 (some-> route-match :parameters :query :id))))

(def display-card-update-hook display-card-enter-hook)

;; Others
(defn login
  "An instance for the login page."
  []
  [views.login/main {:state (app.state/state-cursor :views.login)
                     :save-user-fn #(services.login/set-user! %)
                     :login-fn app.provider/login}])

(defn about
  "An instance for the about page."
  []
  [views.about/main {:fetch-be-version! app.provider/fetch-be-version!}])

(defn header
  "An instance for the headerer component."
  []
  [header/main {::header/email (-> @app.state/state ::lenses.login/current-user ::kws.user/email)}])

(defn new-card-page-props
  []
  {:state (app.state/state-cursor :views.new-card)
   :notify! app.provider/notify!
   :path-to! services.routing/path-to!
   kws.new-card/goto-home! #(services.routing/goto! routing.pages/home)
   kws.new-card/create-card! app.provider/create-card!
   kws.new-card/cards-metadata (lenses.metadata/cards @app.state/state)})

(defn new-card-page
  "An instance for the new-card view."
  []
  [new-card/main (new-card-page-props)])

(defn- profiles-page
  "An instance for the profiles page."
  []
  [views.profiles/main
   {:state (app.state/state-cursor :views.profiles)
    kws.views.profiles/profile-names (-> @app.state/state
                                         lenses.metadata/cards-grid
                                         kws.cards-grid.metadata/profile-names)
    kws.views.profiles/goto-grid! #(services.routing/goto! routing.pages/home)
    kws.views.profiles/load-profile! #(services.cards-grid-profile-manager.route-sync/set-in-route! %)}])

(defn- current-view*
  "Returns an instance of the `current-view` component."
  [state home-view login-view header-component]
  [:<>
   [components.current-view/main
    {::components.current-view/current-user (::lenses.login/current-user state)
     ::components.current-view/view (or (-> state ::lenses.routing/match :data kws.routing/view)
                                        home-view)
     ::components.current-view/login-view login-view
     ::components.current-view/header-component header-component
     ::components.current-view/loading? (-> state lenses.login/initialized? not)}]
   [controllers.action-dispatcher/component]
   [controllers.file-upload-dialog/component]
   [controllers.clipboard-dialog/component]
   [services.notify/toast]])

(defn current-view []
  (current-view* @app.state/state #'controllers.cards-grid/cards-grid login header))


;; -------------------------
;; Routing
(def ^:private routes
  "The reitit-style raw routes."
  (concat
   [["/about"
     {kws.routing/name routing.pages/about
      kws.routing/view #'about}]
    ["/profiles"
      {kws.routing/name routing.pages/profiles
       kws.routing/view #'profiles-page}]
    ["/cards"
     ["/edit"
      {kws.routing/name routing.pages/edit-card
       kws.routing/view #'edit-card-page
       kws.routing/enter-hook edit-card-enter-hook!
       kws.routing/update-hook edit-card-update-hook!}]
     ["/new"
      {kws.routing/name routing.pages/new-card
       kws.routing/view #'new-card-page}]
     ["/display"
      {kws.routing/name routing.pages/display-card
       kws.routing/view #'display-card-page
       kws.routing/enter-hook display-card-enter-hook
       kws.routing/update-hook display-card-update-hook}]]]
   controllers.cards-grid/routes))


;; -------------------------
;; Bus Event Handlers
(defn handle-cards-crud-action
  "Handles actions from cards crud."
  [event-kw {::kws.cards-crud/keys [error-message]}]
  (when (and (not error-message) (kws.cards-crud.actions/cdu event-kw))
    (controllers.cards-grid/refetch!)
    (app.provider/fetch-card-metadata)))

(defn handle-user-logged-in
  "Handles action for an user logging in"
  [event-kw new-user]
  (when (= event-kw kws.services.login/new-user)
    (let [route-match (::lenses.routing/match @app.state/state)]
      (services.cards-grid-profile-manager/fetch-metadata!)
      (app.provider/fetch-card-metadata)
      (services.routing/run-view-hooks! nil route-match)
      (services.cards-grid-profile-manager.route-sync/load-from-route! route-match))))

(defn handle-navigated-to-route
  "Handles action for when the app has navigated to a new route"
  [event-kw args]
  (when (= event-kw kws.routing/action-navigated-to-route)
    (services.logging/set-logging! (app.logging/should-log?))
    (when (services.login/is-logged-in?)
      (let [[old-match new-match] args]
        (services.routing/run-view-hooks! old-match new-match)
        (services.cards-grid-profile-manager.route-sync/react-to-route-change! old-match new-match)))))

(defn handle-new-grid-profile
  "Handles actions for when a new gird profile has been set."
  [event-kw args]
  (when (= event-kw kws.services.cards-grid-profile-manager/action-new-grid-profile)
    (controllers.cards-grid/new-grid-profile! args)))

(def events-bus-handler
  "The main handler for all events send to the event bus."
  #(do
     (handle-cards-crud-action %1 %2)
     (handle-user-logged-in %1 %2)
     (handle-navigated-to-route %1 %2)
     (handle-new-grid-profile %1 %2)))

;; ------------------------------
;; Contextual Action Dispatchers
(defn contextual-actions-dispatcher-hydra-head!
  "Returns the hydra options for the contextual actions dispatcher, based on the current route."
  []
  (when-let [current-route-name (-> @app.state/state lenses.routing/match :data kws.routing/name)]
    (condp = current-route-name

      routing.pages/edit-card
      (edit-card.handlers/hydra-head (edit-card-page-props))

      routing.pages/new-card
      (new-card/hydra-head (new-card-page-props))

      routing.pages/home
      (controllers.cards-grid/hydra-head)

      routing.pages/display-card
      (display-card.handlers/hydra-head (display-card-page-props))

      nil)))

;; ------------------------------
;; Common actions from actions dispatcher
(def global-actions-dispatcher-hydra-head
  "The hydra root head for the action dispatcher."
  {kws.hydra/type kws.hydra/branch
   kws.hydra.branch/name "Action Dispatcher Hydra"
   kws.hydra.branch/heads 
   [{kws.hydra/shortcut    \c
     kws.hydra/description "Cards Grid Configuration"
     kws.hydra/type        kws.hydra/leaf
     kws.hydra.leaf/value #(services.routing/goto! routing.pages/cards-grid-config)}
    {kws.hydra/shortcut    \h
     kws.hydra/description "Home"
     kws.hydra/type        kws.hydra/leaf
     kws.hydra.leaf/value  #(services.routing/goto! routing.pages/home)}
    {kws.hydra/shortcut    \n
     kws.hydra/description "New Cards"
     kws.hydra/type        kws.hydra/leaf
     kws.hydra.leaf/value  #(services.routing/goto! routing.pages/new-card)}
    {kws.hydra/shortcut    \p
     kws.hydra/description "Profile Switcher"
     kws.hydra/type        kws.hydra/leaf
     kws.hydra.leaf/value  #(services.routing/goto! routing.pages/profiles)}
    {kws.hydra/shortcut    \u
     kws.hydra/description "Upload File"
     kws.hydra/type        kws.hydra/leaf
     kws.hydra.leaf/value  #(controllers.file-upload-dialog/upload-file!)}
    {kws.hydra/shortcut    \a
     kws.hydra/description "About the app"
     kws.hydra/type        kws.hydra/leaf
     kws.hydra.leaf/value  #(services.routing/goto! routing.pages/about)}
    {kws.hydra/shortcut    \q
     kws.hydra/description "Quit"
     kws.hydra/type        kws.hydra/leaf
     kws.hydra.leaf/value  #(do)}]})

;; ------------------------------
;; Shortcuts
(def shortcuts
  "All shortcuts to be registered in the app."
  [{kws.services.shortcuts-register/id       ::action-dispatcher
    kws.services.shortcuts-register/key-desc "shift+alt+j"
    kws.services.shortcuts-register/callback #(controllers.action-dispatcher/show!
                                               global-actions-dispatcher-hydra-head)}

   {kws.services.shortcuts-register/id       ::contextual-action-dispatcher
    kws.services.shortcuts-register/key-desc "shift+alt+k"
    kws.services.shortcuts-register/callback #(controllers.action-dispatcher/show!
                                               (contextual-actions-dispatcher-hydra-head!))}])

;; -------------------------
;; Initialize app
(defn mount-root []
  (r.dom/render [current-view] (.getElementById js/document "app")))

(defn ^:export init! []

  ;; Initialize logging first
  (services.logging/set-logging! (app.logging/should-log?))
  
  ;; Initialize services that only depend on logging
  (services.cards-grid-profile-manager/init!
   {:run-http-action-fn
    app.provider/run-http-action
    kws.services.cards-grid-profile-manager/set-metadata-fn!
    #(swap! app.state/state assoc lenses.metadata/cards-grid %)})
  (services.shortcuts-register/init! shortcuts)
  (services.notify/init! {:state (app.state/state-cursor :services.notify)})
  (events-bus/init! {kws.events-bus/handler events-bus-handler})

  ;; Initializes long initialization services
  (services.login/init!
   {:state app.state/state
    :run-http-action-fn app.provider/run-http-action})

  (services.routing/start-routing!
   routes
   (app.state/state-cursor ::lenses.routing/match)
   {kws.routing/global-query-params #{:grid-profile (keyword app.logging/LOGGING_URL_PARAM)}})

  ;; Initializes controllers
  (controllers.action-dispatcher/init!
   {:state (app.state/state-cursor :components.action-dispatcher)})
  (controllers.cards-grid/init!)
  
  (mount-root))
