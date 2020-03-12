(ns ohmycards.web.views.edit-card.handlers
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.services.cards-crud.core :as cards-crud]))

(defn- reduce-before-delete
  "Reduces state before removing a card."
  [state]
  (assoc state kws/loading? true))

(defn- reduce-after-delete
  "Reduces state after removing a card"
  [state {::kws.cards-crud/keys [error-message deleted-card] :as a}]
  (let [card-id (-> state kws/selected-card kws.card/id)]
    (cond-> (assoc state kws/loading? false)
      error-message       (assoc kws/error-message error-message)
      (not error-message) (assoc kws/error-message nil
                                 kws/card-input nil
                                 kws/selected-card nil
                                 kws/good-message (str "Deleted card with id " card-id)))))

(defn delete-card!
  "Handler to delete a card."
  [{:keys [http-fn state]}]
  (swap! state reduce-before-delete)
  (a/go
    (let [resp (a/<! (cards-crud/delete! {:http-fn http-fn}
                                         (-> @state kws/selected-card kws.card/id)))]
      (swap! state reduce-after-delete resp))))

(defn- reduce-before-update
  "Reduces state before removing a card."
  [state]
  (assoc state kws/loading? true))

(defn- reduce-after-update
  "Reduces state after updating a card"
  [state _]
  (assoc state kws/loading? false))

(defn update-card!
  "Handler to update a card."
  [{:keys [http-fn state]}]
  (swap! state reduce-before-update)
  (a/go
    (let [resp (a/<! (cards-crud/update! {:http-fn http-fn} (-> @state kws/card-input)))]
      (swap! state reduce-after-update resp))))
