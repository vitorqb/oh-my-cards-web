(ns ohmycards.web.kws.views.edit-card.core)

;; 
;; Props
;; 
(def ^:const goto-home! "A fn that navigates to the home view." ::goto-home!)
(def ^:const goto-displaycard! "A fn that navigates to the display card view (with id)." ::goto-displaycard!)
(def ^:const fetch-card! "A fn that is used to fetch the card." ::fetch-card!)
(def ^:const update-card! "A fn that is used to update a card." ::update-card!)
(def ^:const delete-card! "A fn that is used to delete a card." ::delete-card!)
(def ^:const cards-metadata "Metadata for cards." ::cards-metadata)
(def ^:const confirm-deletion-fn!
  "A function called with a card to confirm deletion.
   Must return a channel from where return \"true\" or \"false\" will be read."
  ::confirm-deletion-fn!)
(def ^:const user-link-to-card! "A fn that allows the user to get a link to a card after entering a ref." ::user-link-to-card!)

;;
;; State
;; 
(def ^:const selected-card "The original card being edited (selected by the user)" ::selected-card)
(def ^:const card-input "The user-inputted new values for the card being edited" ::card-input)
(def ^:const error-message "An user-friendly error message." ::error-message)
(def ^:const good-message "An user-friendly positive message." ::good-message)
(def ^:const loading? "Whether the view should display the loading state." ::loading?)


