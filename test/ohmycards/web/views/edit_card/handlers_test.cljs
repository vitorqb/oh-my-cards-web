(ns ohmycards.web.views.edit-card.handlers-test
  (:require [cljs.core.async :as async]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.hydra.leaf :as kws.hydra.leaf]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.services.cards-crud.core :as cards-crud]
            [ohmycards.web.views.edit-card.handlers :as sut]
            [ohmycards.web.views.edit-card.state-management :as state-management]
            [reagent.core :as r]))

(deftest test-reduce-before-event

  (testing "Sets loading to true"
    (is (-> {} sut/reduce-before-event kws/loading? true?))))

(deftest test-reduce-after-event

  (testing "Sets loading to false"
    (is (false? (kws/loading? (sut/reduce-after-event {kws/loading? true} {}))))
    (is (false? (kws/loading? (sut/reduce-after-event {kws/loading? true}
                                                      {kws.cards-crud/error-message "err"})))))

  (testing "Set's error message on error"
    (is (= "err"
           (kws/error-message (sut/reduce-after-event {kws/loading? true}
                                                      {kws.cards-crud/error-message "err"})))))

  (testing "Unsets error msg if no error"
    (is (nil? (kws/error-message (sut/reduce-after-event {kws/error-message "err"} {}))))))

(deftest test-delete-async-action

  (testing "Pre reducer"
    (let [async-action (sut/delete-async-action {})
          pre-reducer (kws.async-actions/pre-reducer-fn async-action)]
      (is (= {kws/loading? true
              kws/error-message nil
              kws/good-message nil}
             (pre-reducer {kws/loading? false})))))

  (testing "Post reducer"
    (let [async-action (sut/delete-async-action {})
          post-reducer (kws.async-actions/post-reducer-fn async-action)]

      (testing "Success"
        (let [card {kws.card/id "id"}
              state {kws/loading? true
                     kws/card-input card
                     kws/selected-card card
                     kws/good-message "FOO"}
              response {kws/error-message nil}]
          (is (= {kws/error-message nil
                  kws/loading? false
                  kws/card-input nil
                  kws/selected-card nil
                  kws/good-message (sut/deleted-card-msg card)}
                 (post-reducer state response)))))

      (testing "Failure"
        (let [card {kws.card/id "id"}
              state {kws/loading? true
                     kws/card-input card
                     kws/selected-card card}
              response {kws.cards-crud/error-message "ERROR"}]
          (is (= {kws/error-message "ERROR"
                  kws/loading? false
                  kws/card-input card
                  kws/selected-card card}
                 (post-reducer state response))))))))

(deftest test-reduce-after-update

  (testing "Sets success msg"
    (is (= sut/updated-card-msg (kws/good-message (sut/reduce-after-update {} {})))))

  (testing "Sets selected-card and card-input"
    (let [card {kws.card/id 1}]
      (is (= card
             (kws/selected-card (sut/reduce-after-update {} {kws.cards-crud/updated-card card}))))
      (is (= (state-management/card->form-input card)
             (kws/card-input (sut/reduce-after-update {} {kws.cards-crud/updated-card card})))))))

(deftest test-update-card!

  (testing "If no coercion errors, calls run!"
    (let [run-calls (atom 0)]
      (with-redefs [sut/run-update-card! #(swap! run-calls inc)]
        (let [card-input {kws.card/tags (coercion.result/success ["A"] ["A"])}
              state (r/atom {kws/card-input card-input})]
          (sut/update-card! {:state state})
          (is (= 1 @run-calls))))))

  (testing "If coercion errors, calls warn-user-of-invalid-input!"
    (let [run-calls (atom 0)
          warn-user-calls (atom 0)]
      (with-redefs [sut/run-update-card! #(swap! run-calls inc)
                    sut/warn-user-of-invalid-input! #(swap! warn-user-calls inc)]
        (let [card-input {kws.card/tags (coercion.result/failure ["A"] "ERR")}
              state (r/atom {kws/card-input card-input})]
          (sut/update-card! {:state state})
          (is (= 0 @run-calls))
          (is (= 1 @warn-user-calls)))))))

(deftest test-goto-displaycard!

  (testing "Calls goto-displaycard! prop with the id"
    (let [id                1
          state             (atom {kws/card-input (state-management/card->form-input {kws.card/id id})})
          goto-displaycard! #(do [::result %])
          props             {:state state kws/goto-displaycard! goto-displaycard!}]
      (is (= [::result 1] (sut/goto-displaycard! props))))))

(deftest test-hydra-head

  (let [props {::foo 1}]

    (testing "Save action calls update-card!"
      (with-redefs [sut/update-card! #(do [::update-card %1])]
        (let [save-action (-> props sut/hydra-head kws.hydra.branch/heads first)
              save-action-value (kws.hydra.leaf/value save-action)]
          (is (= \s (kws.hydra/shortcut save-action)))
          (is (= "Save" (kws.hydra/description save-action)))
          (is (= kws.hydra/leaf (kws.hydra/type save-action)))
          (is (= [::update-card props] ((kws.hydra.leaf/value save-action)))))))

    (testing "Delete action calls delete-card!"
      (with-redefs [sut/delete-card! #(do [::delete-card %1])]
        (let [delete-action (-> props sut/hydra-head kws.hydra.branch/heads second)
              delete-action-value (kws.hydra.leaf/value delete-action)]
          (is (= \d (kws.hydra/shortcut delete-action)))
          (is (= "Delete" (kws.hydra/description delete-action)))
          (is (= kws.hydra/leaf (kws.hydra/type delete-action)))
          (is (= [::delete-card props] ((kws.hydra.leaf/value delete-action)))))))

    (testing "View (Display) action calls delete-card!"
      (with-redefs [sut/goto-displaycard! #(do [::goto-displaycard! %1])]
        (let [goto-displaycard-action (-> props sut/hydra-head kws.hydra.branch/heads (get 2))
              goto-displaycard-value (kws.hydra.leaf/value goto-displaycard-action)]
          (is (= \v (kws.hydra/shortcut goto-displaycard-action)))
          (is (= "View (Display)" (kws.hydra/description goto-displaycard-action)))
          (is (= kws.hydra/leaf (kws.hydra/type goto-displaycard-action)))
          (is (= [::goto-displaycard! props] ((kws.hydra.leaf/value goto-displaycard-action)))))))

    (testing "Quit action"
      (let [quit-action (-> props sut/hydra-head kws.hydra.branch/heads (get 3))
            quit-action-value (kws.hydra.leaf/value quit-action)]
        (is (= \q (kws.hydra/shortcut quit-action)))
        (is (= "Quit" (kws.hydra/description quit-action)))
        (is (= kws.hydra/leaf (kws.hydra/type quit-action)))
        (is (nil? ((kws.hydra.leaf/value quit-action))))))))
