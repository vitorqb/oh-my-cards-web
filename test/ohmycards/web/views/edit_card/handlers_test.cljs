(ns ohmycards.web.views.edit-card.handlers-test
  (:require [cljs.core.async :as async]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.card :as kws.card]
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

(deftest test-reduce-after-delete
  
  (testing "Sets loading to false"
    (is (->> {kws/loading? true} (sut/reduce-after-delete {}) kws/loading? false?)))

  (testing "On success"
    (let [state       {kws/loading? true
                       kws/selected-card {kws.card/id 1}
                       kws/card-input {kws.card/id 1}}
          service-res {}
          result      (sut/reduce-after-delete state service-res)]

      (testing "Sets loading to false"
        (is (false? (kws/loading? result))))

      (testing "Clears selected-card"
        (is (nil? (kws/selected-card result))))

      (testing "Clears card-input"
        (is (nil? (kws/card-input result))))

      (testing "Clears error-message"
        (is (nil? (kws/error-message result))))

      (testing "Sets success-message"
        (is (= "Deleted card with id 1" (kws/good-message result)))))))

(deftest test-delete-card!--dont-call-delete-if-confirm-is-false
  (let [card                 {::kws.card/title "Foo"}
        state                (atom {kws/selected-card card})
        cards-crud-args      (atom [])
        confirm-deletion-fn! #(async/go false)
        props                {:state state
                              kws/confirm-deletion-fn! confirm-deletion-fn!
                              kws/delete-card! #(swap! cards-crud-args conj %&)}]
    (let [resp-chan (sut/delete-card! props)]
      (async done
             (async/go
               (is (false? (async/<! resp-chan)))
               (is (= [] @cards-crud-args))
               (done))))))

(deftest test-delete-card!--call-delete-if-conficonfirm-is-true
  (let [card                 {kws.card/title "Foo" kws.card/id "Bar"}
        initial-state        {kws/selected-card card}
        state                (atom initial-state)
        cards-crud-args      (atom [])
        confirm-deletion-fn! #(async/go true)
        props                {:state state
                              kws/confirm-deletion-fn! confirm-deletion-fn!
                              kws/delete-card! #(async/go (swap! cards-crud-args conj %&))}]
    (async done
           (async/go
             (let [resp-chan (sut/delete-card! props)]
               (is (not (false? (async/<! resp-chan))))
               (is (= [["Bar"]] @cards-crud-args))
               (is (= @state (sut/reduce-after-delete initial-state {})))
               (done))))))

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
