(ns ohmycards.web.app.logging
  "Configures the app logging."
  (:require [ohmycards.web.globals :as globals])
  (:import goog.Uri
           goog.Uri.QueryData))

(def ^:const LOGGING_URL_PARAM "logging")

(defn should-log-from-url?
  "Returns true if the current url points to the necessity of logging"
  []
  (let [uri (.parse Uri js/location.href)
        uri-query-data ^goog.Uri.QueryData (.getQueryData uri)
        uri-param (.get uri-query-data LOGGING_URL_PARAM)
        fragment ^String (.getFragment uri)
        fragment-uri ^goog.Uri (.parse Uri fragment)
        fragment-query-data ^goog.Uri.QueryData (.getQueryData fragment-uri)
        fragment-param (.get fragment-query-data LOGGING_URL_PARAM)]
    (cond
     (= uri-param "TRUE") ::true
     (= uri-param "true") ::true
     (= fragment-param "TRUE") ::true
     (= fragment-param "true") ::true
     (= uri-param "FALSE") ::false
     (= uri-param "false") ::false
     (= fragment-param "FALSE") ::false
     (= fragment-param "false") ::false
     :else nil)))

(defn should-log? []
  (case (should-log-from-url?)
    ::true true
    ::false false
    nil globals/LOG_ENABLED))
