(ns ohmycards.web.components.header.core)

(defn main
  "A header component for the app.
  - `email`: The email of the user (displayed on the right corner)"
  [{::keys [email]}]
  [:div.header {}
   [:span.header__left "OhMyCards!"]
   [:span.header__center ""]
   [:span.header__right email]])
