(ns ohmycards.web.core
  (:require [cljs.core.async :as async]
            [ohmycards.web.common.utils :as utils]
            [ohmycards.web.components.current-view.core :as components.current-view]
            [ohmycards.web.components.app-header.core :as header]
            [ohmycards.web.controllers.action-dispatcher.core
             :as
             controllers.action-dispatcher]
            [ohmycards.web.controllers.cards-grid.core :as controllers.cards-grid]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.cards-grid.metadata.core :as kws.cards-grid.metadata]
            [ohmycards.web.kws.http :as kws.http]
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
            [ohmycards.web.services.cards-crud.core :as services.cards-crud]
            [ohmycards.web.services.cards-grid-profile-manager.core
             :as
             services.cards-grid-profile-manager]
            [ohmycards.web.services.cards-metadata-fetcher.core
             :as
             services.cards-metadata-fetcher]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.services.fetch-cards.core :as services.fetch-cards]
            [ohmycards.web.services.http :as services.http]
            [ohmycards.web.services.login.core :as services.login]
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
            [reagent.core :as r]
            [reagent.dom :as r.dom]))

;; -------------------------
;; State
(defonce state (r/atom {}))

(defn- state-cursor [path] (r/cursor state (utils/to-path path)))

;; -------------------------
;; Http helpers
(defn http-fn
  "Wraps the http service function injecting the token from the global state."
  [& args]
  (let [token (-> @state lenses.login/current-user kws.user/token)]
    (apply services.http/http kws.http/token token args)))

(defn fetch-cards!
  "A shortcut to fetch cards using the fetch-cards svc"
  [opts]
  (services.fetch-cards/main (assoc opts :http-fn http-fn)))

;; -------------------------
;; Services > Cards Grid Profile Manager
(def cards-grid-profile-manager-opts
  {:http-fn
   http-fn

   kws.services.cards-grid-profile-manager/on-metadata-fetch
   #(swap! state assoc lenses.metadata/cards-grid %)})

;; -------------------------
;; Services > Card CRUD
(defn fetch-card! [id] (services.cards-crud/read! {:http-fn http-fn} id))

;; -------------------------
;; Metadata
(defn fetch-card-metadata
  []
  (let [metadata-chan (services.cards-metadata-fetcher/main {:http-fn http-fn})]
    (async/go
      (swap! state assoc lenses.metadata/cards (async/<! metadata-chan)))))

;; -------------------------
;; View instances

;; Edit card
(defn edit-card-page-props
  "Props for the `edit-card-page`."
  []
  {kws.edit-card/goto-home! #(services.routing/goto! routing.pages/home)
   kws.edit-card/fetch-card! fetch-card!
   kws.edit-card/cards-metadata (lenses.metadata/cards @state)
   kws.edit-card/confirm-deletion-fn! services.user-question/confirm-card-delete
   :http-fn http-fn
   :state (state-cursor :views.edit-card)})

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
  {:state (state-cursor :views.display-card)
   kws.display-card/fetch-card! fetch-card!})

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
  [views.login/main {:state (state-cursor :views.login)
                     :http-fn http-fn
                     :save-user-fn #(services.login/set-user! %)}])

(defn about
  "An instance for the about page."
  []
  [views.about/main {:http-fn http-fn}])

(defn header
  "An instance for the headerer component."
  []
  [header/main {::header/email (-> @state ::lenses.login/current-user ::kws.user/email)}])

(defn cards-grid-page
  "An instance for the cards-grid view."
  []
  [controllers.cards-grid/cards-grid])

(defn new-card-page-props
  []
  {:http-fn http-fn
   :state (state-cursor :views.new-card)
   kws.new-card/goto-home! #(services.routing/goto! routing.pages/home)
   kws.new-card/cards-metadata (lenses.metadata/cards @state)})

(defn new-card-page
  "An instance for the new-card view."
  []
  [new-card/main (new-card-page-props)])

(defn cards-grid-config-page
  "An instance for the cards-grid-config view."
  []
  (let [profile-names  (-> @state lenses.metadata/cards-grid kws.cards-grid.metadata/profile-names)
        cards-metadata (-> @state lenses.metadata/cards)
        props          {kws.cards-grid.config-dashboard/profiles-names profile-names
                        kws.cards-grid.config-dashboard/cards-metadata cards-metadata}]
    [controllers.cards-grid/config-dashboard-page props]))

(defn- current-view*
  "Returns an instance of the `current-view` component."
  [state home-view login-view header-component]
  [:<>
   [components.current-view/main
    {::components.current-view/current-user     (::lenses.login/current-user state)
     ::components.current-view/view             (or (-> state ::lenses.routing/match :data kws.routing/view)
                                                    home-view)
     ::components.current-view/login-view       login-view
     ::components.current-view/header-component header-component}]
   [controllers.action-dispatcher/component]])

(defn current-view [] (current-view* @state cards-grid-page login header))


;; -------------------------
;; Routing
(def ^:private routes
  "The reitit-style raw routes."
  [["/"
    {kws.routing/name routing.pages/home
     kws.routing/view #'cards-grid-page}]
   ["/about"
    {kws.routing/name routing.pages/about
     kws.routing/view #'about}]
   ["/cards-grid/config"
    {kws.routing/name routing.pages/cards-grid-config
     kws.routing/view #'cards-grid-config-page}]
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
      kws.routing/update-hook display-card-update-hook}]]])


;; -------------------------
;; Bus Event Handlers
(defn handle-cards-crud-action
  "Handles actions from cards crud."
  [event-kw {::kws.cards-crud/keys [error-message]}]
  (when (and (not error-message) (kws.cards-crud.actions/cdu event-kw))
    (controllers.cards-grid/refetch!)
    (fetch-card-metadata)))

(defn handle-user-logged-in
  "Handles action for an user logging in"
  [event-kw new-user]
  (when (= event-kw kws.services.login/new-user)
    (services.cards-grid-profile-manager/fetch-metadata! cards-grid-profile-manager-opts)
    (fetch-card-metadata)
    (controllers.cards-grid/load-profile-from-route-match! (::lenses.routing/match @state))
    ;; Give a change for views that depends on login to initialize themselves.
    (services.routing/force-update!)))

(defn handle-navigated-to-route
  "Handles action for when the app has navigated to a new route"
  [event-kw route-match]
  (when (= event-kw kws.routing/action-navigated-to-route)
    (when (services.login/is-logged-in?)
      (controllers.cards-grid/load-profile-from-route-match! route-match))))

(def events-bus-handler
  "The main handler for all events send to the event bus."
  #(do
     (handle-cards-crud-action %1 %2)
     (handle-user-logged-in %1 %2)
     (handle-navigated-to-route %1 %2)))

;; ------------------------------
;; Contextual Action Dispatchers
(defn contextual-actions-dispatcher-hydra-head!
  "Returns the hydra options for the contextual actions dispatcher, based on the current route."
  []
  (when-let [current-route-name (-> @state lenses.routing/match :data kws.routing/name)]
    (condp = current-route-name

      routing.pages/edit-card
      (edit-card.handlers/hydra-head (edit-card-page-props))

      routing.pages/new-card
      (new-card/hydra-head (new-card-page-props))

      routing.pages/home
      (controllers.cards-grid/hydra-head)

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

  ;; Initialize services
  (services.login/init! {:state state :http-fn http-fn})

  (events-bus/init! {kws.events-bus/handler events-bus-handler})

  (services.routing/start-routing! routes (state-cursor ::lenses.routing/match))

  (services.shortcuts-register/init! shortcuts)

  ;; Initializes controllers
  (controllers.action-dispatcher/init! {:state (state-cursor :components.action-dispatcher)})

  (controllers.cards-grid/init!
   {:app-state state
    :http-fn http-fn
    :cards-grid-profile-manager-opts cards-grid-profile-manager-opts})

  (mount-root))
 
