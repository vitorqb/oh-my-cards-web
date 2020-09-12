(ns ohmycards.web.components.inputs.textarea
  (:require [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]))

(defn main
  "A textarea reusable input."
  [{:keys [value on-change] :as props}]
  [:textarea.textarea
   {:value value
    :on-change #(some-> % .-target .-value on-change)}])

(defmethod inputs/impl kws.inputs/t-textarea [props]
  [main props])
