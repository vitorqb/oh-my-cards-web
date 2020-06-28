(ns ohmycards.web.kws.views.display-card.core)

;; 
;; Props
;; 
(def fetch-card! "Function (id -> channel<Card>) used to fetch the card." ::fetch-card!)

;;
;; State
;; 
(def loading? "Boolean indicating loading status." ::loading?)
(def card "The fetched card" ::card)
(def error-message "An error message to the user" ::error-message)
