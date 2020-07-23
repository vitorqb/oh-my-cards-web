(ns ohmycards.web.views.display-card.handlers
  (:require [cljs.core.async :as async]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.hydra.leaf :as kws.hydra.leaf]
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

(defn- goto-editcard!
  "Navigates to the page that edit the current card."
  [{:keys [state] ::kws/keys [goto-editcard!]}]
  (-> @state kws/card kws.card/id goto-editcard!))

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

(defn hydra-head
  "Returns an hydra head for the contextual actions dispatcher."
  [props]
  {kws.hydra/type         kws.hydra/branch
   kws.hydra.branch/name  "Edit Card Hydra"
   kws.hydra.branch/heads [{kws.hydra/shortcut    \e
                            kws.hydra/description "Edit"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(goto-editcard! props)}
                           {kws.hydra/shortcut    \q
                            kws.hydra/description "Quit"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(do)}]})
