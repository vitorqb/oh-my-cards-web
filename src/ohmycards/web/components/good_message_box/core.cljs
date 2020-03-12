(ns ohmycards.web.components.good-message-box.core)

(defn main
  "A box for displaying good messages to the user."
  [{:keys [value]}]
  (when value
    [:span.good-message-box {}
     value]))
