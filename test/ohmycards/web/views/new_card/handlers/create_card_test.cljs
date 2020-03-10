(ns ohmycards.web.views.new-card.handlers.create-card-test
  (:require [ohmycards.web.views.new-card.handlers.create-card :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.services.cards-crud.core :as services.cards-crud]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.card :as kws.card]))

(deftest test-should-create?

  (testing "False if loading"
    (is (false? (sut/should-create? {kws/loading? true}))))

  (testing "True if not loading"
    (is (true? (sut/should-create? {kws/loading? false})))))

(deftest test-before-create

  (testing "Set's loading to true"
    (is (true? (kws/loading? (sut/before-create {})))))

  (testing "Resets error message"
    (is (nil? (kws/error-message (sut/before-create {kws/error-message "foo"}))))))

(deftest test-after-create

  (testing "Set's loading to false"
    (is (false? (-> {kws/loading? true} (sut/after-create {}) kws/loading?))))

  (testing "Unset created-card"
    (is (nil? (-> {kws/created-card ::foo} (sut/after-create {}) kws/created-card))))

  (testing "Set's error message if any"
    (is (= "errmsg"
           (kws/error-message (sut/after-create {} {kws.cards-crud/error-message "errmsg"})))))

  (testing "Set's created card"
    (let [card {kws.card/id "FOO"}]
      (is (= card
             (->> {kws.cards-crud/created-card card} (sut/after-create {}) kws/created-card))))))
