(ns ohmycards.web.components.inputs.textarea)

(defn main
  "A textarea reusable input."
  [{:keys [value on-change] :as props}]
  [:textarea.textarea
   {:value value
    :on-change #(some-> % .-target .-value on-change)}])
