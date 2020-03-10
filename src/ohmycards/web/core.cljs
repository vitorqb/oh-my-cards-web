(ns ohmycards.web.core
  (:require
   [reagent.core :as r]
   [ohmycards.web.kws.routing.pages :as routing.pages]
   [ohmycards.web.routing.core :as routing.core]
   [ohmycards.web.kws.lenses.routing :as lenses.routing]
   [ohmycards.web.kws.lenses.login :as lenses.login]
   [ohmycards.web.components.current-view.core :as components.current-view]
   [ohmycards.web.views.login.core :as views.login]
   [ohmycards.web.common.focused-atom :as focused-atom]
   [ohmycards.web.services.http :as services.http]
   [ohmycards.web.services.login.core :as services.login]
   [ohmycards.web.kws.user :as kws.user]
   [ohmycards.web.kws.http :as kws.http]
   [ohmycards.web.components.header.core :as header]
   [ohmycards.web.views.cards-grid.core :as cards-grid]
   [ohmycards.web.views.cards-grid.state-management :as cards-grid.state-management]
   [ohmycards.web.services.fetch-cards.core :as services.fetch-cards]
   [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
   [ohmycards.web.views.cards-grid.config-dashboard.core :as cards-grid.config-dashboard]
   [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws.cards-grid.config-dashboard]))

;; -------------------------
;; State
(defonce state (r/atom {}))

(defn- gen-focused-state
  "Generates a new `FocusedAtom` for the global state object."
  [path]
  (let [path* (if (keyword? path) [path] path)]
    (focused-atom/->FocusedAtom state path*)))


;; -------------------------
;; Http helpers
(defn http-fn
  "Wraps the http service function injecting the token from the global state."
  [& args]
  (let [token (-> @state lenses.login/current-user kws.user/token)]
    (apply services.http/http kws.http/token token args)))

;; -------------------------
;; View instances
(defn login
  "An instance for the login page."
  []
  [views.login/main {:state (gen-focused-state :views.login)
                     :http-fn http-fn
                     :save-user-fn #(swap! state assoc lenses.login/current-user %)}])

(defn header
  "An instance for the headerer component."
  []
  [header/main {::header/email (-> @state ::lenses.login/current-user ::kws.user/email)}])

(def cards-grid-page-props
  "Props given to the cards-grid-page."
  {:state (r/cursor state [:views.cards-grid])
   kws.cards-grid/fetch-cards! #(services.fetch-cards/main (assoc % :http-fn http-fn))
   kws.cards-grid/goto-settings! #(routing.core/goto! routing.pages/cards-grid-config)})

(defn cards-grid-page
  "An instance for the cards-grid view."
  []
  [cards-grid/main cards-grid-page-props])

(defn cards-grid-config-page
  "An instance for the cards-grid-config view."
  []
  [cards-grid.config-dashboard/main
   {:state
    (r/cursor state [:views.cards-grid.config-dashboard])

    kws.cards-grid.config-dashboard/goto-cards-grid!
    #(routing.core/goto! routing.pages/home)

    kws.cards-grid.config-dashboard/set-page!
    #(cards-grid.state-management/set-page-from-props! cards-grid-page-props %)

    kws.cards-grid.config-dashboard/set-page-size!
    #(cards-grid.state-management/set-page-size-from-props! cards-grid-page-props %)}])

(defn- current-view*
  "Returns an instance of the `current-view` component."
  [state home-view login-view header-component]
  [components.current-view/main
   {::components.current-view/current-user     (::lenses.login/current-user state)
    ::components.current-view/view             (or (-> state ::lenses.routing/match :data :view)
                                                   home-view)
    ::components.current-view/login-view       login-view
    ::components.current-view/header-component header-component}])

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
     :view #'cards-grid-config-page}]])

(defn- set-routing-match!
  "Set's the routing match on the state."
  [match]
  (swap! state assoc ::lenses.routing/match match))


;; -------------------------
;; Initialize app
(defn mount-root []
  (r/render [current-view] (.getElementById js/document "app")))

(defn ^:export init! []
  (routing.core/start-routing! routes set-routing-match!)
  (services.login/init-state! {:state state :http-fn http-fn})
  (mount-root))
