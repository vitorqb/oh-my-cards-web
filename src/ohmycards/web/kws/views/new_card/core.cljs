(ns ohmycards.web.kws.views.new-card.core)

;; Props
(def goto-home! "Fn that navigates to home page." ::goto-home!)
(def create-card! "A fn used to create a card." ::create-card!)
(def cards-metadata "Metadata for cards" ::cards-metadata)

;; State
(def card-input "A map with the user-inputed values for the card" ::card-input)
(def loading? "Boolean indicating whether we are loading something in the page." ::loading?)
(def error-message "An error message for the user, if any." ::error-message)
(def created-card "The created card, if any." ::created-card)
