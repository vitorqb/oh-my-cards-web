(ns ohmycards.web.views.cards-grid.config-dashboard.config-parser
  (:require [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]))

(defn to-coercion-result
  "Transforms the value for a given config keyword into a coercion result object."
  [kw value]
  (condp contains? kw
    #{kws.config/exclude-tags kws.config/include-tags kws.config/tags-filter-query}
    (coercion.result/success value value)

    #{kws.config/page kws.config/page-size}
    (coercion.result/success (str value) value)))
