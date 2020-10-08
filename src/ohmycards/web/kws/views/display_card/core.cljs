(ns ohmycards.web.kws.views.display-card.core)

;; 
;; Props
;; 
(def ^:const fetch-card! "Function (id -> channel<Card>) used to fetch the card." ::fetch-card!)
(def ^:const goto-home! "A fn that navigates to the home view." ::goto-home!)
(def ^:const goto-editcard! "A fn that navigates to the edit card (given an id)." ::goto-editcard!)

;;
;; State
;; 
(def ^:const loading? "Boolean indicating loading status." ::loading?)
(def ^:const card "The fetched card" ::card)
(def ^:const error-message "An error message to the user" ::error-message)
