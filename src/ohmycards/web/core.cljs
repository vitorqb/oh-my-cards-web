(ns ohmycards.web.core
  (:require [cljs.core.async :as async]
            [ohmycards.web.common.utils :as utils]
            [ohmycards.web.components.current-view.core :as components.current-view]
            [ohmycards.web.components.header.core :as header]
            [ohmycards.web.controllers.action-dispatcher.core
             :as
             controllers.action-dispatcher]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.hydra.leaf :as kws.hydra.leaf]
            [ohmycards.web.kws.lenses.login :as lenses.login]
            [ohmycards.web.kws.lenses.routing :as lenses.routing]
            [ohmycards.web.kws.routing.core :as kws.routing]
            [ohmycards.web.kws.routing.pages :as routing.pages]
            [ohmycards.web.kws.services.cards-crud.actions
             :as
             kws.cards-crud.actions]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.services.events-bus.core :as kws.events-bus]
            [ohmycards.web.kws.services.shortcuts-register.core
             :as
             kws.services.shortcuts-register]
            [ohmycards.web.kws.user :as kws.user]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core
             :as
             kws.cards-grid.config-dashboard]
            [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [ohmycards.web.kws.views.edit-card.core :as kws.edit-card]
            [ohmycards.web.kws.views.new-card.core :as kws.new-card]
            [ohmycards.web.routing.core :as routing.core]
            [ohmycards.web.services.cards-crud.core :as services.cards-crud]
            [ohmycards.web.services.cards-grid-profile-loader.core
             :as
             services.cards-grid-profile-loader]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.services.fetch-cards.core :as services.fetch-cards]
            [ohmycards.web.services.http :as services.http]
            [ohmycards.web.services.login.core :as services.login]
            [ohmycards.web.services.shortcuts-register.core
             :as
             services.shortcuts-register]
            [ohmycards.web.views.cards-grid.config-dashboard.core
             :as
             cards-grid.config-dashboard]
            [ohmycards.web.views.cards-grid.config-dashboard.state-management
             :as
             cards-grid.config-dashboard.state-management]
            [ohmycards.web.views.cards-grid.core :as cards-grid]
            [ohmycards.web.views.cards-grid.state-management
             :as
             cards-grid.state-management]
            [ohmycards.web.views.edit-card.core :as edit-card]
            [ohmycards.web.views.edit-card.state-management
             :as
             edit-card.state-management]
            [ohmycards.web.views.login.core :as views.login]
            [ohmycards.web.views.new-card.core :as new-card]
            [reagent.core :as r]))

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
;; View instances
(defn login
  "An instance for the login page."
  []
  [views.login/main {:state (state-cursor :views.login)
                     :http-fn http-fn
                     :save-user-fn #(swap! state assoc lenses.login/current-user %)}])

(defn header
  "An instance for the headerer component."
  []
  [header/main {::header/email (-> @state ::lenses.login/current-user ::kws.user/email)}])

(def cards-grid-page-props
  "Props given to the cards-grid-page."
  {:state (state-cursor :views.cards-grid)
   kws.cards-grid/fetch-cards! fetch-cards!
   kws.cards-grid/goto-settings! #(routing.core/goto! routing.pages/cards-grid-config)
   kws.cards-grid/goto-newcard! #(routing.core/goto! routing.pages/new-card)
   kws.cards-grid/goto-editcard! #(routing.core/goto!
                                   routing.pages/edit-card
                                   kws.routing/query-params {:id (kws.card/id %)})})

(defn cards-grid-page
  "An instance for the cards-grid view."
  []
  [cards-grid/main cards-grid-page-props])

(defn new-card-page
  "An instance for the new-card view."
  []
  [new-card/main {:http-fn http-fn
                  :state (state-cursor :views.new-card)
                  kws.new-card/goto-home! #(routing.core/goto! routing.pages/home)}])

(defn edit-card-page
  "An instance for the edit-card view"
  []
  [edit-card/main {kws.edit-card/goto-home! #(routing.core/goto! routing.pages/home)
                   :http-fn http-fn
                   :state (edit-card.state-management/init!
                           (state-cursor :views.edit-card)
                           (-> @state lenses.routing/match :parameters :query :id)
                           #(services.cards-crud/read! {:http-fn http-fn} %))}])

(defn cards-grid-config-page
  "An instance for the cards-grid-config view."
  []
  (let [state (state-cursor :views.cards-grid.config-dashboard)]
    [cards-grid.config-dashboard/main
     {:state
      state

      kws.cards-grid.config-dashboard/goto-cards-grid!
      #(routing.core/goto! routing.pages/home)

      kws.cards-grid.config-dashboard/set-page!
      #(cards-grid.state-management/set-page-from-props! cards-grid-page-props %)

      kws.cards-grid.config-dashboard/set-page-size!
      #(cards-grid.state-management/set-page-size-from-props! cards-grid-page-props %)

      kws.cards-grid.config-dashboard/set-include-tags!
      #(cards-grid.state-management/set-include-tags-from-props! cards-grid-page-props %)

      kws.cards-grid.config-dashboard/set-exclude-tags!
      #(cards-grid.state-management/set-exclude-tags-from-props! cards-grid-page-props %)

      ;; !!!! TODO pass real options
      kws.cards-grid.config-dashboard/profiles-names
      ["OhMyCards Base" "OhMyCards Done"]

      kws.cards-grid.config-dashboard/load-profile!
      #(async/go
         (let [chan (services.cards-grid-profile-loader/main! {:http-fn http-fn} %)
               resp (async/<! chan)]
           (cards-grid.config-dashboard.state-management/set-config-from-loader! state resp)
           (cards-grid.state-management/set-config-from-loader! cards-grid-page-props resp)))}]))

(defn- current-view*
  "Returns an instance of the `current-view` component."
  [state home-view login-view header-component]
  [:<>
   [components.current-view/main
    {::components.current-view/current-user     (::lenses.login/current-user state)
     ::components.current-view/view             (or (-> state ::lenses.routing/match :data :view)
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
    {:name routing.pages/home
     :view #'cards-grid-page}]
   ["/cards-grid/config"
    {:name routing.pages/cards-grid-config
     :view #'cards-grid-config-page}]
   ["/cards"
    ["/edit"
     {:name routing.pages/edit-card
      :view #'edit-card-page}]
    ["/new"
     {:name routing.pages/new-card
      :view #'new-card-page}]]])

(defn- set-routing-match!
  "Set's the routing match on the state."
  [match]
  (swap! state assoc ::lenses.routing/match match))


;; -------------------------
;; Bus Event Handlers
(defn handle-cards-crud-action
  "Handles actions from cards crud."
  [event-kw {::kws.cards-crud/keys [error-message]}]
  (when (and (not error-message) (kws.cards-crud.actions/cdu event-kw))
    (cards-grid.state-management/refetch-from-props! cards-grid-page-props)))

(def events-bus-handler
  "The main handler for all events send to the event bus."
  #(do
     (handle-cards-crud-action %1 %2)))

;; ------------------------------
;; Shortcuts
(def shortcuts
  "All shortcuts to be registered in the app."
  [{kws.services.shortcuts-register/id       ::action-dispatcher
    kws.services.shortcuts-register/key-desc "shift+alt+j"
    kws.services.shortcuts-register/callback #(controllers.action-dispatcher/show!)}
   {kws.services.shortcuts-register/id       ::hello
    kws.services.shortcuts-register/key-desc "shift+alt+h"
    kws.services.shortcuts-register/callback #(js/alert "Hello!")}])


;; ------------------------------
;; Common actions from actions dispatcher
(def actions-dispatcher-hydra-options
  "The hydra root head for the action dispatcher."
  {kws.hydra/type kws.hydra/branch
   kws.hydra.branch/name "Action Dispatcher Hydra"
   kws.hydra.branch/heads 
   [{kws.hydra/shortcut    \c
     kws.hydra/description "Cards Grid Configuration"
     kws.hydra/type        kws.hydra/branch
     kws.hydra.branch/name "Cards Grid Configuration"
     kws.hydra.branch/heads
     [{kws.hydra/shortcut    \l
       kws.hydra/description "Load Profile"
       kws.hydra/type        kws.hydra/leaf
       kws.hydra.leaf/value  #(do)}
      {kws.hydra/shortcut    \s
       kws.hydra/description "Save Profile"
       kws.hydra/type        kws.hydra/leaf
       kws.hydra.leaf/value  #(do)}
      {kws.hydra/shortcut    \g
       kws.hydra/description "Go!"
       kws.hydra/type        kws.hydra/leaf
       kws.hydra.leaf/value #(routing.core/goto! routing.pages/cards-grid-config)}]}
    {kws.hydra/shortcut    \h
     kws.hydra/description "Home"
     kws.hydra/type        kws.hydra/leaf
     kws.hydra.leaf/value  #(routing.core/goto! routing.pages/home)}
    {kws.hydra/shortcut    \q
     kws.hydra/description "Quit"
     kws.hydra/type        kws.hydra/leaf
     kws.hydra.leaf/value  #(do)}]})

;; -------------------------
;; Initialize app
(defn mount-root []
  (r/render [current-view] (.getElementById js/document "app")))

(defn ^:export init! []
  ;; Services
  (services.login/init-state! {:state state :http-fn http-fn})
  (events-bus/init! {kws.events-bus/handler events-bus-handler})
  (routing.core/start-routing! routes set-routing-match!)
  (services.shortcuts-register/init! shortcuts)
  (controllers.action-dispatcher/init!
   {:state (state-cursor :components.action-dispatcher)
    :actions-dispatcher-hydra-options actions-dispatcher-hydra-options})
  (mount-root))
