(ns ohmycards.web.kws.views.find-card.core)

;; Props
(def ^:const fetch-card! "A fn that fetches a card (see services.cards-crud)" ::fetch-card!)
(def ^:const goto-displaycard! "A fn that routes the app to the display card page, accepting an id as param." ::goto-displaycard!)

;; State
(def ^:const value "The current value of the input" ::value)
(def ^:const error-message "The current value for the error message" ::error-message)
(def ^:const disabled? "Are the submit and input currently disabled?" ::disabled?)
