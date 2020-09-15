(ns ohmycards.web.views.edit-card.queries
  (:require [ohmycards.web.common.coercion.core :as coercion]
            [ohmycards.web.kws.views.edit-card.core :as kws]))

(defn input-error?
  "Returns true if there is an error from the user input"
  [{:keys [state]}]
  (-> @state kws/card-input coercion/extract-values nil?))
