(ns ohmycards.web.kws.views.edit-card.core)

(def goto-home! "A fn that navigates to the home view." ::goto-home!)
(def fetch-card! "A fn that is used to fetch the card." ::fetch-card!)
(def cards-metadata "Metadata for cards." ::cards-metadata)
(def confirm-deletion-fn!
  "A function called with a card to confirm deletion.
   Must return a channel from where return \"true\" or \"false\" will be read."
  ::confirm-deletion-fn!)

(def selected-card "The original card being edited (selected by the user)" ::selected-card)
(def card-input "The user-inputted new values for the card being edited" ::card-input)
(def error-message "An user-friendly error message." ::error-message)
(def good-message "An user-friendly positive message." ::good-message)

(def loading? "Whether the view should display the loading state." ::loading?)


