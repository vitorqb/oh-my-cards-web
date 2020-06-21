(ns ohmycards.web.components.spinner.core)

(defn main
  "A spinner."
  [_]
  [:div.spinner])

(defn small [_] [:div.spinner.spinner--small])
(defn smaller [_] [:div.spinner.spinner--smaller])
(defn smallest [_] [:div.spinner.spinner--smallest])
