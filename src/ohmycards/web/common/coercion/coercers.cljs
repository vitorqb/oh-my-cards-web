(ns ohmycards.web.common.coercion.coercers
  (:refer-clojure :exclude [empty not-empty])
  (:require [ohmycards.web.common.coercion.result :as result]
            [ohmycards.web.common.tags.core :as tags]
            [ohmycards.web.common.utils :as utils]
            [ohmycards.web.kws.common.coercion.result :as kws.result]))

;; Constants
(def not-an-integer "Not an integer!")
(def not-positive "Not a positive number!")
(def not-empty "Expected an empty value!")
(def not-valid-tags "Invalid values for tags!")
(def not-in-acceptable-vals "Value is not among the acceptable values.")
(def not-valid-string "Not a valid string.")
(def not-min-length "Does not has the minimal required length!.")

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

(def string
  "Coerces to string."
  (wrap-success
   (fn [{::kws.result/keys [value] :as result}]
     (try (assoc result kws.result/value (str value))
          (catch js/Error e
            (result/->failure result not-valid-string))))))

(defn min-length
  "Factory for a validator that checks if a result has a min length."
  [min-length]
  (wrap-success
   (fn [{::kws.result/keys [value] :as result}]
     (or (try (if (>= (count value) min-length) result)
              (catch js/Error e nil))
         (result/->failure result not-min-length)))))

(def tags
  "Validator and coercer that fails if what it receives is not valid tags. Also eliminates
  empty strings from the tags."
  (wrap-success
   (fn [{::kws.result/keys [value] :as result}]
     (or
      (if (seqable? value)
        (let [value' (into [] (remove empty? value))]
          (if (every? tags/valid? value')
            (assoc result kws.result/value value'))))
      (result/->failure result not-valid-tags)))))

(defn is-in
  "Factory for a Validator that fails if a value is not inside a set of values."
  [acceptable-values]
  (wrap-success
   (fn [{::kws.result/keys [value] :as result}]
     (if ((set acceptable-values) value)
       result
       (result/->failure result not-in-acceptable-vals)))))

