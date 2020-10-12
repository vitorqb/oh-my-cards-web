(ns ohmycards.web.components.card-history-displayer.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.card-history-displayer.core :as sut]
            [ohmycards.web.components.card-history-displayer.field-update-displayer
             :as
             field-update-displayer]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.components.table.core :as table]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.common.cards.history.core :as kws.cards.history]
            [ohmycards.web.kws.components.card-history-displayer.core :as kws]
            [ohmycards.web.kws.components.card-history-displayer.field-update-displayer
             :as
             kws.field-update-displayer]
            [ohmycards.web.kws.components.table.column :as kws.table.column]
            [ohmycards.web.kws.components.table.core :as kws.table]
            [ohmycards.web.kws.components.table.row :as kws.table.row]
            [ohmycards.web.kws.services.card-history-fetcher.core
             :as
             kws.card-history-fetcher]
            [ohmycards.web.test-utils :as tu]))

(deftest test-fetch-history-async-action
  (let [async-action (sut/fetch-history-async-action {} "id")
        pre-reducer-fn (kws.async-actions/pre-reducer-fn async-action)
        action-fn (kws.async-actions/action-fn async-action)
        post-reducer-fn (kws.async-actions/post-reducer-fn async-action)]

    (testing "Pre reducer > set's loading"
      (is (= {kws/loading? true} (pre-reducer-fn {kws/loading? false}))))

    (testing "Post reducer > Success"
      (let [dstate {kws/loading? true}
            result {kws.card-history-fetcher/success? true
                    kws.card-history-fetcher/history ::history}]
        (is (= {kws/loading? false
                kws/history ::history}
               (post-reducer-fn dstate result)))))

    (testing "Post reducer > Failure"
      (let [dstate {kws/loading? true}
            result {kws.card-history-fetcher/success? false
                    kws.card-history-fetcher/error-message "error-msg"}]
        (is (= {kws/loading? false
                kws/error-message "error-msg"}
               (post-reducer-fn dstate result)))))))

(deftest test-event-details-row

  (testing "event-update"
    (let [field-update-1 {kws.cards.history/field-name "Field 1"}
          field-update-2 {kws.cards.history/field-name "Field 2"}
          event {kws.cards.history/event-type kws.cards.history/event-update
                 kws.cards.history/field-updates [field-update-1 field-update-2]}
          props {kws.table/row {::sut/event event}}
          comp (sut/event-details-row props)]
      (is
       (=
        [:<>
         [[field-update-displayer/main {kws.field-update-displayer/field-update field-update-1}]
          [field-update-displayer/main {kws.field-update-displayer/field-update field-update-2}]]]
        comp)))))

(deftest test-events-table

  (let [events [{kws.cards.history/datetime "2020-10-09T13:03:21.358Z"
                 kws.cards.history/event-type kws.cards.history/event-creation}]
        history {kws.cards.history/events events}
        props {:state (atom {kws/history history})}
        comp (sut/events-table props)
        table-props (tu/get-props-for table/main (tu/comp-seq comp))]

    (is (= {kws.table/columns
            [{kws.table.column/keyword :datetime
              kws.table.column/name "Datetime"}
             {kws.table.column/keyword :event-type
              kws.table.column/name "Event Type"}]
            
            kws.table/rows
            [{kws.table.row/values {:datetime "2020-10-09T13:03:21.358Z"
                                    :event-type [sut/event-type {::sut/event (first events)}]
                                    :actions [:div]}
              ::sut/event (first events)}]

            kws.table/row-details-comp [sut/event-details-row props]}
           table-props))))

(deftest test-main

  (testing "Renders loading wrapper"
    (let [comp (tu/comp-seq (sut/main {:state (atom {kws/loading? true})}))
          loading-wrapper-props (tu/get-props-for loading-wrapper/main comp)]
      (is (true? (:loading? loading-wrapper-props)))))

  (testing "Renders events table"
    (let [state (atom {kws/history {kws.cards.history/events [::event1 ::event2]}})
          props {:state state}
          comp (tu/comp-seq (sut/main props))]
      (is (tu/exists-in-component?
           [sut/events-table props]
           comp)))))
