(ns ohmycards.web.services.http.utils
  (:require [clojure.string :as string]))

(defn body->err-msg
  "Extracts an error message from an http response body."
  [body default-error-message]
  (if (empty? body) default-error-message body))

(defn list->query-arg
  "Transforms a list into a query argument by joining it with commas."
  [lst]
  (and lst (string/join "," lst)))
