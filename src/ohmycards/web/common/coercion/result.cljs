(ns ohmycards.web.common.coercion.result
  (:require [ohmycards.web.kws.common.coercion.result :as kws]))

(defn raw-value->success
  "Returns a successfull result from a raw value."
  [raw-value]
  {kws/value raw-value kws/raw-value raw-value kws/success? true})

(defn success
  "Returns a successfull result from a value and raw value."
  [raw-value value]
  {kws/value value kws/raw-value raw-value kws/success? true})

(defn failure
  "Returns a failure result from a raw-value and error-message"
  [raw-value error-message]
  {kws/raw-value raw-value kws/success? false kws/error-message error-message})

(defn ->failure
  "Maps a success into a failure with a given error message."
  [result error-message]
  (-> result
      (assoc kws/error-message error-message kws/success? false)
      (dissoc kws/value)))
