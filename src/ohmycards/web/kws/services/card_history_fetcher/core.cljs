(ns ohmycards.web.kws.services.card-history-fetcher.core)

;; Result of call
(def success? "True if the response is a success, false otherwise." ::success?)
(def error-message "An error message for this service. Only available if `success?` is false." ::error-message)
(def history "The value of the history. Only available if `success?` is true." ::history)
