(ns ohmycards.web.kws.services.cards-crud.actions
  (:refer-clojure :exclude [update]))

(def create ::create)
(def read ::read)
(def delete ::delete)
(def update ::update)

(def all #{create read delete update})
(def cdu #{create delete update})
