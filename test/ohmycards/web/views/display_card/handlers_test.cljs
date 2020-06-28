(ns ohmycards.web.views.display-card.handlers-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.services.cards-crud.core :as kws.services.cards-crud]
            [ohmycards.web.kws.views.display-card.core :as kws]
            [ohmycards.web.views.display-card.handlers :as sut]))

(deftest test-reduce-before-fetch

  (testing "Set's loading"
    (is (true? (kws/loading? (sut/reduce-before-fetch-card {})))))

  (testing "Unsets card"
    (is (nil? (kws/card (sut/reduce-before-fetch-card {kws/card 1})))))

  (testing "Unsets error message"
    (is (nil? (kws/error-message (sut/reduce-before-fetch-card {kws/error-message "A"}))))))

(deftest test-reduce-after-fetch

  (let [good-response {kws.services.cards-crud/read-card {:title "A"}}
        bad-response  {kws.services.cards-crud/error-message "B"}]

    (testing "Set's loading to false"
      (is (false? (kws/loading? (sut/reduce-after-fetch-card {} {})))))

    (testing "Set's fetched card"
      (is (= {:title "A"} (kws/card (sut/reduce-after-fetch-card {} good-response)))))

    (testing "Set's error message"
      (is (= "B" (kws/error-message (sut/reduce-after-fetch-card {} bad-response)))))))

