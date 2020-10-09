(ns ohmycards.web.common.cards.history.core
  (:require [ohmycards.web.kws.common.cards.history.core :as kws]))

(defn- event-type-from-http
  "Parses an even type from http"
  [x]
  (case x
    "update"   kws/event-update
    "creation" kws/event-creation
    "deletion" kws/event-deletion
    (do (js/console.warn (str "Unknown event type " x))
        nil)))

(defn- field-type-from-http
  "Parses a field update type from http"
  [x]
  (case x
    "string" kws/field-string
    "tags"   kws/field-tags
    (do (js/console.warn (str "Unknown field type " x))
        nil)))

(defn- field-update-from-http
  "Parses an event update from http"
  [x]
  {kws/field-name (:fieldName x)
   kws/field-type (-> x :fieldType field-type-from-http)
   kws/old-value  (:oldValue x)
   kws/new-value  (:newValue x)})

(defn- event-from-http
  "Parses an entire card historical event from http"
  [x]
  {kws/datetime (:datetime x)
   kws/event-type (-> x :eventType event-type-from-http)
   kws/field-updates (->> x :fieldUpdates (map field-update-from-http))})

(defn from-http
  "Parses the payload of an http response containing a card history."
  [x]
  {kws/events (map event-from-http (:history x))})
