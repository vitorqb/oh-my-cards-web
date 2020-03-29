(ns ohmycards.web.common.coercion.coercers
  (:refer-clojure :exclude [empty not-empty])
  (:require [ohmycards.web.common.coercion.result :as result]
            [ohmycards.web.common.utils :as utils]
            [ohmycards.web.kws.common.coercion.result :as kws.result]))

;; Constants
(def not-an-integer "Not an integer!")
(def not-positive "Not a positive number!")
(def not-empty "Expected an empty value!")

;; Helpers methods to construct coercers
(defn wrap-success
  "Wraps a fn that is only called if the value it reseives is a successfull validation,
  and false otherwise."
  [f]
  (fn [result]
    (if (kws.result/success? result)
      (f result)
      result)))

(def integer
  "Coercer that coerces a result to an integer."
  (wrap-success
   (fn [{::kws.result/keys [value] :as result}]
     (cond
       (string? value) (if-let [value' (utils/str->integer value)]
                         (assoc result kws.result/value value')
                         (result/->failure result not-an-integer))

       (integer? value) result

       :else (result/->failure result not-an-integer)))))

(def positive
  "Validator that fails a result if it is not a positive number."
  (wrap-success
   (fn [{::kws.result/keys [value] :as result}]
     (if (pos? value)
       result
       (result/->failure result not-positive)))))

(def empty
  "Validator that checks if a result is empty and coerces it to nil"
  (wrap-success
   (fn [{::kws.result/keys [value] :as result}]
     (if (and (seqable? value) (empty? value))
       (assoc result kws.result/value nil)
       (result/->failure result not-empty)))))
