(ns ohmycards.web.components.card-history-displayer.core
  (:require [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.components.table.core :as table]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.common.cards.history.core :as kws.cards.history]
            [ohmycards.web.kws.components.card-history-displayer.core :as kws]
            [ohmycards.web.kws.components.table.column :as kws.table.column]
            [ohmycards.web.kws.components.table.core :as kws.table]
            [ohmycards.web.kws.components.table.row :as kws.table.row]
            [ohmycards.web.kws.services.card-history-fetcher.core
             :as
             kws.card-history-fetcher]))

;; Actions
(defn fetch-history-async-action [{:keys [state] ::kws/keys [fetch-card-history!]} card-id]
  {kws.async-actions/state
   state

   kws.async-actions/pre-reducer-fn
   #(assoc % kws/loading? true)

   kws.async-actions/action-fn
   #(fetch-card-history! card-id)

   kws.async-actions/post-reducer-fn
   (fn [dstate {::kws.card-history-fetcher/keys [success? history error-message]}]
     (cond-> dstate
       :always       (assoc kws/loading? false)
       success?      (assoc kws/history history)
       error-message (assoc kws/error-message error-message)))})

;; Components
(defn- event-type
  "Renders the type of the event"
  [{::keys [event]}]
  (case (kws.cards.history/event-type event)
    ::kws.cards.history/event-creation [:span "Created"]
    ::kws.cards.history/event-update   [:span "Updated"]
    ::kws.cards.history/event-deletion [:span "Deleted"]))

(defn- event-details
  "Thunkified  Renders the details for an event."
  [props]
  (let [event (-> props kws.table/row ::event)]
    (with-out-str (cljs.pprint/pprint event))))

(defn- events-table
  "A component to display a table with all events."
  [props]
  [table/main
   {kws.table/columns [{kws.table.column/keyword :datetime
                        kws.table.column/name "Datetime"}
                       {kws.table.column/keyword :event-type
                        kws.table.column/name "Event Type"}]
    kws.table/rows (for [event (-> props :state deref kws/history kws.cards.history/events)
                         :let [datetime (kws.cards.history/datetime event)
                               event-type [event-type {::event event}]
                               actions [:div]]]
                     {kws.table.row/values {:datetime datetime
                                            :event-type event-type
                                            :actions actions}
                      ::event event})
    kws.table/row-details-comp [event-details props]}])

(defn main
  "A component that knows how to display the history of a card."
  [{:keys [state] :as props}]
  (let [dstate @state loading? (kws/loading? dstate)]
    [loading-wrapper/main {:loading? loading?}
     [:div.card-history-displayer
      [:h4 "Changelog"]
      [events-table props]]]))
