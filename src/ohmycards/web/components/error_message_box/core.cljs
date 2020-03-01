(ns ohmycards.web.components.error-message-box.core)

(defn main
  "A box for displaying error messages to the user."
  [{:keys [value]}]
  (when value
    [:div.error-message-box {}
     value]))
