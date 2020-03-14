(ns ohmycards.web.views.edit-card.handlers-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.views.edit-card.handlers :as sut]))

(deftest test-reduce-before-delete

  (testing "Sets loading to true"
    (is (-> {} sut/reduce-before-delete kws/loading? true?))))

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

(deftest test-reduce-before-update

  (testing "Sets loading to true"
    (is (-> {} sut/reduce-before-update kws/loading? true?))))
