(ns ohmycards.web.components.current-view.core
  (:require [ohmycards.web.services.login.utils :as login.utils]))

(defn main 
  "A component to render the application current page.
  - `current-user`: The current user, or nil if no user.
  - `view`: A reagent component for the current view.
  - `login-view`: The page to render if the user is not logged in.
  - `header-component`: The header to use on every page."
  [{:keys [state] ::keys [current-user view view-props login-view header-component loading?] :as props}]
  (when-not loading?
    [:div.current-view
     (if (login.utils/user-logged-in? current-user)
       (list ^{:key :header} [header-component]
             ^{:key :view}   [view view-props])
       [login-view])]))
