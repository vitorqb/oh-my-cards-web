(ns ohmycards.web.components.current-view.core)

(defn main 
  "A component to render the application current page.
  - `current-user`: The current user, or nil if no user.
  - `view`: A reagent component for the current view.
  - `login-view`: The page to render if the user is not logged in.
  - `header-component`: The header to use on every page."
  [{:keys [state] ::keys [current-user view login-view header-component] :as props}]
  [:div.current-view
   (if current-user
     (list ^{:key :header} [header-component]
           ^{:key :view}   [view])
     [login-view])])
