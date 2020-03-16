(ns ohmycards.web.services.http.utils)

(defn body->err-msg
  "Extracts an error message from an http response body."
  [body default-error-message]
  (if (empty? body) default-error-message body))
