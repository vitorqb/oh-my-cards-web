(ns ohmycards.web.views.edit-card.handlers
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.services.cards-crud.core :as cards-crud]))

(defn- deleted-card-msg [c]
  (str "Deleted card with id " (kws.card/id c)))

(def ^:private updated-card-msg "Successfully updated!")

(defn- reduce-before-event
  "Generic state transformation before an event.."
  [state]
  (assoc state
         kws/loading? true
         kws/error-message nil
         kws/good-message nil))

(defn- reduce-after-event
  "Generic state transformation after an event..."
  [state {::kws.cards-crud/keys [error-message]}]
  (cond-> (assoc state kws/loading? false)
    error-message       (assoc kws/error-message error-message)
    (not error-message) (assoc kws/error-message nil)))

(defn- reduce-after-delete
  "Reduces state after removing a card"
  [state {::kws.cards-crud/keys [error-message deleted-card] :as result}]
  (cond-> (reduce-after-event state result)
    (not error-message) (assoc kws/card-input nil
                               kws/selected-card nil
                               kws/good-message (-> state kws/selected-card deleted-card-msg))))

(defn delete-card!
  "Handler to delete a card."
  [{:keys [http-fn state]}]
  (swap! state reduce-before-event)
  (a/go
    (let [resp (a/<! (cards-crud/delete! {:http-fn http-fn}
                                         (-> @state kws/selected-card kws.card/id)))]
      (swap! state reduce-after-delete resp))))

(defn- reduce-after-update
  "Reduces state after updating a card"
  [state {::kws.cards-crud/keys [error-message updated-card] :as result}]
  (cond-> (reduce-after-event state result)
    (not error-message) (assoc kws/good-message updated-card-msg
                               kws/card-input updated-card
                               kws/selected-card updated-card)))

(defn update-card!
  "Handler to update a card."
  [{:keys [http-fn state]}]
  (swap! state reduce-before-event)
  (a/go
    (let [resp (a/<! (cards-crud/update! {:http-fn http-fn} (-> @state kws/card-input)))]
      (swap! state reduce-after-update resp))))
