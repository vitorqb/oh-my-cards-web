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
  (protocols.http/multipart-params [_] [["file" file]])
  (protocols.http/do-after! [_ _ key]
    ((::to-clipboard! opts) (url-for-key key))))

(defn ask-for-file!
  "Asks the user for a file to be uploaded."
  []
  (let [el        (js/document.createElement "input")
        file-chan (a/chan 1)]
    (set! (.-type el) "file")
    (set! (.-hidden el) true)
    (set! (.-onchange el)
          (fn [_]
            (a/go
              (if-let [file (->> el .-files js->clj first)]
                (a/>! file-chan file)
                (a/close! file-chan)))))
    (js/document.body.appendChild el)
    (.click el)
    (js/document.body.removeChild el)
    file-chan))

(defn ask-and-upload [opts]
  "Asks the user for a file and uploads it"
  (let [ask-for-file!   (::ask-for-file!   opts ask-for-file!)
        notify!         (::notify!         opts (constantly nil))
        run-http-action (::run-http-action opts (constantly nil))]
    (a/go
      (when-let [file (a/<! (ask-for-file!))]
        (notify! (str "Uploading file: " (.-name file) "..."))
        (run-http-action (->FileUploadHttpAction file opts))))))
