(ns ohmycards.web.services.user-question.core
  "A service responsible to asking questions to users."
  (:require [cljs.core.async :as async]
            [ohmycards.web.kws.card :as kws.card]))

(defn yes-no
  "Prompts the user for a yes/no question. Returns a channel with true/false."
  [prompt]
  (async/go (js/confirm prompt)))

(defn confirm-card-delete
  "Prompts the user to delete a card."
  [card]
  (yes-no (str "Are you sure you want to delete " (kws.card/title card) "?")))
