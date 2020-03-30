(ns ohmycards.web.kws.common.coercion.result)

(def success? "Boolean, whether the coercion was a success or not." ::success?)
(def error-message "String, the error message for this result." ::error-message)
(def raw-value "The raw value before coercion." ::raw-value)
(def value "The value after coercion." ::value)
