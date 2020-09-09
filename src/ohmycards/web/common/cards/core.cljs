(ns ohmycards.web.common.cards.core
  (:require [ohmycards.web.common.tags.core :as tags]
            [ohmycards.web.kws.card :as kws.card]))

(defn to-http
  "Parses a card so it can be sent in an http request."
  [{::kws.card/keys [id title body tags]}]
  {:id id
   :title title
   :body body
   :tags (tags/sanitize tags)})

(defn from-http
  "Parses a card from an http response."
  [{:keys [id title body tags createdAt updatedAt ref]}]
  {kws.card/id         id
   kws.card/title      title
   kws.card/body       body
   kws.card/tags       tags
   kws.card/created-at createdAt
   kws.card/updated-at updatedAt
   kws.card/ref        ref})
