(ns ohmycards.web.views.edit-card.handlers-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.views.edit-card.handlers :as sut]))

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

(deftest test-reduce-after-update

  (testing "Sets success msg"
    (is (= sut/updated-card-msg (kws/good-message (sut/reduce-after-update {} {})))))

  (testing "Sets selected-card and card-input"
    (let [card {kws.card/id 1}]
      (is (= card
             (kws/selected-card (sut/reduce-after-update {} {kws.cards-crud/updated-card card}))))
      (is (= card
             (kws/card-input (sut/reduce-after-update {} {kws.cards-crud/updated-card card})))))))
