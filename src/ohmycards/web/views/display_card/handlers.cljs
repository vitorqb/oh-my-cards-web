(ns ohmycards.web.views.display-card.handlers
  (:require [cljs.core.async :as async]
            [ohmycards.web.kws.services.cards-crud.core :as kws.services.cards-crud]
            [ohmycards.web.kws.views.display-card.core :as kws]))

;; 
;; Helpers
;;
(defn- reduce-before-fetch-card
  "Reduces the state before fetching the card."
  [state]
  (assoc state kws/loading? true kws/card nil kws/error-message nil))

(defn- reduce-after-fetch-card
  "Reduces the state after fetching the card."
  [state response]
  (let [success? (-> response kws.services.cards-crud/read-card nil? not)]
    (cond-> (assoc state kws/loading? false)
      success? (assoc kws/card (kws.services.cards-crud/read-card response))
      (not success?) (assoc kws/error-message (kws.services.cards-crud/error-message response)))))

;;
;; API
;; 
(defn init!
  "Initializes the state. The first argument are the props, and the
  second argument is the card id that must be displayed."
  [{:keys [state] ::kws/keys [fetch-card!] :as props} card-id]
  (swap! state reduce-before-fetch-card)
  (async/go
    (swap! state reduce-after-fetch-card (async/<! (fetch-card! card-id)))))
