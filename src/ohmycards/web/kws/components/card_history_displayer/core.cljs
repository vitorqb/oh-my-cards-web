(ns ohmycards.web.kws.components.card-history-displayer.core)

;; Props
(def ^:const fetch-card-history! "A fn called to fetch the card history. Must return a channel with a history fetch result (see `ohmycards.web.kws.services.card-history-fetcher.core`) " ::fetch-card-history!)

;; State
(def ^:const history "The current card history." ::history)
(def ^:const loading? "True if the component is loading." ::loading?)
(def ^:const error-message "An error message to display." ::error-message)
