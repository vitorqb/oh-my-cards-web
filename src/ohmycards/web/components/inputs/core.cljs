(ns ohmycards.web.components.inputs.core
  (:require [ohmycards.web.common.coercion.core :as coercion]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.common.coercion.coercers :as coercion.coercers]
            [ohmycards.web.kws.common.coercion.result :as kws.coercion.result]
            [ohmycards.web.kws.components.inputs.core :as kws]))

(defn- parse-props
  "Parses the received props into the props understood by the inputs implementations."
  [{::kws/keys [itype props cursor coercer disabled? auto-focus]}]
  (let [maybe-coerce (if coercer #(coercion/main % coercer) identity)
        maybe-extract-value (if coercer kws.coercion.result/raw-value identity)]
    (cond-> props
      :always (assoc kws/itype (or itype kws/t-simple))
      :always (assoc :on-change #(->> % maybe-coerce (reset! cursor)))
      :always (assoc :value (maybe-extract-value @cursor))
      (not (nil? disabled?)) (assoc :disabled disabled?)
      (not (nil? auto-focus)) (assoc :auto-focus auto-focus))))

(defmulti impl
  "Implementation for specific input types. Dispatches on kws/itype."
  kws/itype)

(defn main
  "Wrapper component for rendering an input."
  [props]
  [impl (parse-props props)])
