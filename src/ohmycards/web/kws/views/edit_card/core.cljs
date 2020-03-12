(ns ohmycards.web.kws.views.edit-card.core)

(def goto-home! "A fn that navigates to the home view." ::goto-home!)

(def selected-card "The original card being edited (selected by the user)" ::selected-card)
(def card-input "The user-inputted new values for the card being edited" ::card-input)
(def error-message "An user-friendly error message." ::error-message)
(def good-message "An user-friendly positive message." ::good-message)

(def loading? "Whether the view should display the loading state." ::loading?)


