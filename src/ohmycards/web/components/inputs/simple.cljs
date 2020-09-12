(ns ohmycards.web.components.inputs.simple
  (:require [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]))

(defn- gen-input-on-change-handler
  "Returns a on-change handler that calls `on-change` with the new input value."
  [on-change]
  (fn [event]
    (on-change (.-value (.-target event)))))

(defn main
  "An input for a form, usually comming alone in a form row."
  [props]
  [:input
   (-> props
       (update :on-change gen-input-on-change-handler)
       (update :class #(or % "simple-input")))])

(defmethod inputs/impl kws.inputs/t-simple
  [props]
  [main props])
