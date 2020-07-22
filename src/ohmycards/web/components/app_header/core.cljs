(ns ohmycards.web.components.app-header.core)

(defn main
  "A header component for the app.
  - `email`: The email of the user (displayed on the right corner)"
  [{::keys [email]}]
  [:div.app-header {}
   [:span.app-header__left "OhMyCards!"]
   [:span.app-header__center ""]
   [:span.app-header__right email]])
