(ns ohmycards.web.kws.views.display-card.core)

;; 
;; Props
;; 
(def fetch-card! "Function (id -> channel<Card>) used to fetch the card." ::fetch-card!)
(def goto-home! "A fn that navigates to the home view." ::goto-home!)
(def goto-editcard! "A fn that navigates to the edit card (given an id)." ::goto-editcard!)

;;
;; State
;; 
(def loading? "Boolean indicating loading status." ::loading?)
(def card "The fetched card" ::card)
(def error-message "An error message to the user" ::error-message)
