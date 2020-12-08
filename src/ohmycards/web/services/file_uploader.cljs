(ns ohmycards.web.services.file-uploader
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.protocols.http :as protocols.http]))

(defn- url-for-key
  "Returns the url for a given static assets key"
  [key]
  (str "/api/v1/staticassets/" key))

(defrecord FileUploadHttpAction [file opts]
  protocols.http/HttpAction
  (protocols.http/url [_] "/v1/staticassets")
  (protocols.http/method [_] :post)
  (protocols.http/parse-success-response [_ r] (-> r kws.http/body :key))
  (protocols.http/multipart-params [_] [["file" file]]))

(defn ask-and-upload [opts]
  "Asks the user for a file and uploads it"
  (let [ask-for-file!   (::ask-for-file!   opts (constantly nil))
        notify!         (::notify!         opts (constantly nil))
        run-http-action (::run-http-action opts (constantly nil))
        to-clipboard!   (::to-clipboard!   opts (constantly nil))]
    (a/go
      (when-let [file (a/<! (ask-for-file!))]
        (notify! (str "Uploading file: " (.-name file) "..."))
        (let [key (-> (->FileUploadHttpAction file opts) run-http-action a/<!)]
          (to-clipboard! (url-for-key key)))))))
