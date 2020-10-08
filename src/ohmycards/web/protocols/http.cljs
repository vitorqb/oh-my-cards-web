(ns ohmycards.web.protocols.http)

;; An http action represents the action of sending an http request to an url to fetch
;; some information, and then parsing it with a parsing function.
(defprotocol HttpAction
  (method [_] "A keyword with the http method to use.")
  (url [_] "The url to use.")
  (json-params [_] "The json parameters to send.")
  (query-params [_] "The query parameters to send.")
  (parse-success-response [_ response] "Receives a success response for parsing.")
  (parse-error-response [_ response] "Receives an error response for parsing.")
  (do-after! [_ response parsed-response] "Side-effect hook called with the response after we receive it."))

(extend-type default
  HttpAction
  (method [_] :GET)
  (url [_] "/")
  (json-params [_] nil)
  (query-params [_] nil)
  (parse-success-response [_ x] x)
  (parse-error-response [_ x] x)
  (do-after! [_ _ _] nil))
