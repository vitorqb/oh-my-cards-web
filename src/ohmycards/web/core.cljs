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
   [ohmycards.web.services.http :as services.http]))

;; -------------------------
;; State
(defonce state (r/atom {}))

(defn- gen-focused-state
  "Generates a new `FocusedAtom` for the global state object."
  [path]
  (let [path* (if (keyword? path) [path] path)]
    (focused-atom/->FocusedAtom state path*)))


;; -------------------------
;; View instances
(defn login
  "An instance for the login page."
  []
  [views.login/main {:state (gen-focused-state :views.login)
                     :http-fn #(apply services.http/http %&)
                     :save-user-fn #(swap! state assoc lenses.login/current-user %)}])

(defn home-page
  "An instance for the home page."
  []
  [:div [:h2 "Home page."]])

(defn- current-view*
  "Returns an instance of the `current-view` component."
  [state home-view login-view]
  [components.current-view/main
   {::components.current-view/current-user (::lenses.login/current-user state)
    ::components.current-view/view         (or (-> state ::lenses.routing/match :view) home-view)
    ::components.current-view/login-view   login-view}])

(defn current-view [] (current-view* @state home-page login))


;; -------------------------
;; Routing
(def ^:private routes
  "The reitit-style raw routes."
  [["/"
    {:name ::routing.pages/home
     :view #'home-page}]])

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
  (mount-root))
