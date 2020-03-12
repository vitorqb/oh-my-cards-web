(ns ohmycards.web.kws.views.new-card.core)

(def goto-home! "Fn that navigates to home page." ::goto-home!)

(def card-input "A map with the user-inputed values for the card" ::card-input)
(def loading? "Boolean indicating whether we are loading something in the page." ::loading?)
(def error-message "An error message for the user, if any." ::error-message)
(def created-card "The created card, if any." ::created-card)
