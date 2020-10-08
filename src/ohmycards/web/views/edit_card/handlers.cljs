(ns ohmycards.web.views.edit-card.handlers
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.async-actions.core :as async-action]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.hydra.leaf :as kws.hydra.leaf]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.services.cards-crud.core :as cards-crud]
            [ohmycards.web.views.edit-card.queries :as queries]
            [ohmycards.web.views.edit-card.state-management :as state-management]))

(def INVALID_FORM_MSG "ERROR: The form contains invalid data preventing the update!")

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

(defn- delete-async-action [{:keys [state] ::kws/keys [confirm-deletion-fn! delete-card!]}]
  {kws.async-actions/state
   state

   kws.async-actions/action-fn
   #(-> % kws/selected-card kws.card/id delete-card!)

   kws.async-actions/run-condition-fn
   #(-> % kws/selected-card confirm-deletion-fn!)

   kws.async-actions/pre-reducer-fn
   reduce-before-event

   kws.async-actions/post-reducer-fn
   (fn [s {::kws.cards-crud/keys [error-message deleted-card] :as result}]
     (cond-> (reduce-after-event s result)
       (not error-message)
       (assoc kws/card-input nil
              kws/selected-card nil
              kws/good-message (-> s kws/selected-card deleted-card-msg))))})

(defn delete-card!
  "Handler to delete a card."
  [props]
  (async-action/run (delete-async-action props)))

(defn- reduce-after-update
  "Reduces state after updating a card"
  [state {::kws.cards-crud/keys [error-message updated-card] :as result}]
  (cond-> (reduce-after-event state result)
    (not error-message) (assoc kws/good-message updated-card-msg
                               kws/card-input (state-management/card->form-input updated-card)
                               kws/selected-card updated-card)))

(defn- run-update-card!
  "Runs the update of a card."
  [{:keys [state] ::kws/keys [update-card!]}]
  (swap! state reduce-before-event)
  (a/go
    (let [card (-> @state kws/card-input state-management/form-input->card)
          resp (a/<! (update-card! card))]
      (swap! state reduce-after-update resp))))

(defn- warn-user-of-invalid-input!
  "Warns the user of invalid input that prevents update of the card."
  [props]
  ((:notify! props) INVALID_FORM_MSG))

(defn update-card!
  "Handler to update a card."
  [props]
  (if (queries/input-error? props)
    (warn-user-of-invalid-input! props)
    (run-update-card! props)))

(defn goto-displaycard!
  "Navigates to the page that displays the card."
  [{:keys [state] ::kws/keys [goto-displaycard!]}]
  (-> @state kws/card-input state-management/form-input->card kws.card/id goto-displaycard!))

(defn hydra-head
  "Returns an hydra head for the contextual actions dispatcher."
  [props]
  {kws.hydra/type         kws.hydra/branch
   kws.hydra.branch/name  "Edit Card Hydra"
   kws.hydra.branch/heads [{kws.hydra/shortcut    \s
                            kws.hydra/description "Save"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(update-card! props)}
                           {kws.hydra/shortcut    \d
                            kws.hydra/description "Delete"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(delete-card! props)}
                           {kws.hydra/shortcut    \v
                            kws.hydra/description "View (Display)"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(goto-displaycard! props)}
                           {kws.hydra/shortcut    \q
                            kws.hydra/description "Quit"
                            kws.hydra/type        kws.hydra/leaf
                            kws.hydra.leaf/value  #(do)}]})
