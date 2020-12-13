(ns ohmycards.web.kws.views.find-card.core)

;; Props
(def ^:const fetch-card! "A fn that fetches a card (see services.cards-crud)" ::fetch-card!)
(def ^:const goto-displaycard! "A fn that routes the app to the display card page, accepting an id as param. The second param is an obj of options, in which `:storage-key` may be set. This key tells the `display-card` page that it's safe to read from that key instead of fetching the card from the BE." ::goto-displaycard!)
(def ^:const storage-put! "A fn that set's an object into the storage, returning a key. See services.storage." ::storage-put!)

;; State
(def ^:const value "The current value of the input" ::value)
(def ^:const error-message "The current value for the error message" ::error-message)
(def ^:const disabled? "Are the submit and input currently disabled?" ::disabled?)
