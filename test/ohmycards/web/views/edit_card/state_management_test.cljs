(ns ohmycards.web.views.edit-card.state-management-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.views.edit-card.state-management :as sut]))

(deftest test-should-fetch-card?

  (testing "Don't fetch if we are currently fetching it"
    (is
     (false?
      (sut/should-fetch-card? {::sut/is-fetching? true} "foo"))))

  (testing "Don't fetch if card is already fetched"
    (is
     (false?
      (sut/should-fetch-card? {::sut/last-fetched-card-id "id"} "id"))))

  (testing "Fetch if ids are different"
    (is
     (true?
      (sut/should-fetch-card? {kws/selected-card {kws.card/id "1"}} "2")))))

(deftest test-reduce-before-card-fetch

  (testing "Assocs is-fetching?"
    (is (true? (-> {} (sut/reduce-before-card-fetch "id") ::sut/is-fetching?))))

  (testing "Assocs last-fetched-card-id"
    (is (= "id" (-> {} (sut/reduce-before-card-fetch "id") ::sut/last-fetched-card-id)))))

(deftest test-reduce-on-card-fetch

  (testing "With error"
    (let [result (sut/reduce-on-card-fetch {} {kws.cards-crud/error-message "err"})]

      (testing "Sets is-fetching? to false"
        (is (false? (::sut/is-fetching? result))))

      (testing "Set's error message"
        (is (= "err" (kws/error-message result))))

      (testing "Sets loading to false"
        (is (false? (kws/loading? result))))))

  (testing "Success"

    (let [card        {kws.card/id 1}
          service-res {kws.cards-crud/read-card card}
          result (sut/reduce-on-card-fetch {} service-res)]
    
      (testing "Sets card input on success"
        (is (= card (kws/card-input result))))

      (testing "Sets selected card on success"
        (is (= card (kws/selected-card result))))

      (testing "Sets is-fetching? to false"
        (is (false? (::sut/is-fetching? result))))

      (testing "Sets loading to false"
        (is (false? (kws/loading? result)))))))
