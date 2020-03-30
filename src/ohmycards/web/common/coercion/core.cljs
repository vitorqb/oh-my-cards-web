(ns ohmycards.web.common.coercion.core
  (:require [ohmycards.web.common.coercion.result :as result]
            [ohmycards.web.kws.common.coercion.result :as kws.result]))


(defprotocol Coercer
  "The basic figure of a coerce, that takes a `coercion/result` and
  returns a new `coercion/result`."
  (-coerce [this result] "Performs coercion from `result` to `result`."))

;; Any function mapping result -> result can be a coercer
(extend-type function Coercer
  (-coerce [this result] (this result)))

;; A coercer that tries to apply a sequence of coercers to a value, and returns the first
;; successfull one. Otherwise returns the last failed one.
(defrecord Or [coercers]
  Coercer
  (-coerce [_ result]
    (assert (> (count coercers) 0) "At least one coercer must be given!")
    (if-not (kws.result/success? result)
      result
      (loop [_coercer (first coercers) _coercers (rest coercers) _result result]
        (if-not _coercer
          _result
          (let [_result' (-coerce _coercer result)]
            (if (kws.result/success? _result')
              _result'
              (recur (first _coercers) (rest _coercers) _result'))))))))

;; API
(defn main
  "Coerces a value."
  [value coercer]
  (-coerce coercer (result/raw-value->success value)))
