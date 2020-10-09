(ns ohmycards.web.views.new-card.queries
  (:require [ohmycards.web.common.coercion.core :as coercion]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.common.coercion.result :as kws.coercion.result]
            [ohmycards.web.kws.views.new-card.core :as kws]))

(defn card-form-input
  "Returns the coerced inputted card by the user."
  [state]
  (-> state kws/card-input coercion/extract-values))

(defn form-has-errors?
  "Returns true if the form has errors, false otherwise."
  [state]
  (-> state kws/card-input coercion/extract-values nil?))
