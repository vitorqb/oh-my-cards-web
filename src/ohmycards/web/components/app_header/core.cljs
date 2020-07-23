(ns ohmycards.web.components.app-header.core
  (:require [ohmycards.web.components.header.core :as header]))

(defn main
  "A header component for the app.
  - `email`: The email of the user (displayed on the right corner)"
  [{::keys [email]}]
  [header/main {:extra-class "app-header"
                :left        [:span.logo "OhMyCards!"]
                :right       [:span.u-italic email]}])
