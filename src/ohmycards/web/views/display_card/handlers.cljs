(ns ohmycards.web.views.display-card.handlers
  (:require [cljs.core.async :as async]
            [ohmycards.web.common.async-actions.core :as async-action]
            [ohmycards.web.common.cards.core :as cards]
            [ohmycards.web.components.card-history-displayer.core
             :as
             card-history-displayer]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.hydra.leaf :as kws.hydra.leaf]
            [ohmycards.web.kws.services.cards-crud.core :as kws.services.cards-crud]
            [ohmycards.web.kws.views.display-card.core :as kws]
            [ohmycards.web.views.display-card.child.card-history-displayer
             :as
             child.card-history-displayer]))

;; 
;; Helpers
;;
(defn fetch-card-async-action
  [{:keys [state] ::kws/keys [fetch-card! storage-peek!]} {:keys [card-id storage-key]}]
  {kws.async-actions/state
   state

   kws.async-actions/pre-reducer-fn
   #(assoc % kws/loading? true kws/card nil kws/error-message nil)

   kws.async-actions/action-fn
   (fn [_]
     (if-let [card (some-> storage-key storage-peek!)]
       {kws.services.cards-crud/read-card card}
       (fetch-card! card-id)))

   kws.async-actions/post-reducer-fn
   (fn [state response]
     (let [success? (-> response kws.services.cards-crud/read-card nil? not)
           read-card (kws.services.cards-crud/read-card response)
           error-message (kws.services.cards-crud/error-message response)]
       (cond-> (assoc state kws/loading? false)
         success? (assoc kws/card read-card)
         (not success?) (assoc kws/error-message error-message))))})

;;
;; API
;; 
(defn goto-editcard!
  "Navigates to the page that edit the current card."
  [{:keys [state] ::kws/keys [goto-editcard!]}]
  (-> @state kws/card kws.card/id goto-editcard!))

(defn copy-to-clipboard!
  "Copies the card title to the clipboard."
  [{:keys [state] ::kws/keys [to-clipboard!]}]
  (-> @state kws/card cards/->title to-clipboard!))

(defn init!
  "Initializes the component, setting the state and fetching both the card and history.
   Can receive either a card `card-id` or `card-ref` for deciding the card to fetch.
   If `storage-key` is given, reads the card from storage using this key instead of fetch."
  [props {:keys [card-id card-ref storage-key] :as opts}]
  (swap! (:state props) {})
  (swap! (:state (child.card-history-displayer/get-props props)) {})
  (async-action/run (fetch-card-async-action props opts))
  (async-action/run (child.card-history-displayer/fetch-history-async-action props card-id)))

(defn hydra-head
  "Returns an hydra head for the contextual actions dispatcher."
  [props]
  {kws.hydra/type         kws.hydra/branch
   kws.hydra.branch/name  "Edit Card Hydra"
   kws.hydra.branch/heads [{kws.hydra/shortcut    \e
                            kws.hydra/description "Edit"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(goto-editcard! props)}
                           {kws.hydra/shortcut    \c
                            kws.hydra/description "Copy"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(copy-to-clipboard! props)}
                           {kws.hydra/shortcut    \q
                            kws.hydra/description "Quit"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(do)}]})
