(ns ohmycards.web.views.new-card.handlers.create-card-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.views.new-card.handlers.create-card :as sut]
            [reagent.core :as r]))

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

(deftest test-warn-user-of-invalid-input!

  (testing "Notifies user"
    (let [notify! #(do [::notify! %])]
      (is (= [::notify! sut/INVALID_FORM_MSG]
             (sut/warn-user-of-invalid-input! {:notify! notify!}))))))

(deftest test-main

  (testing "Warns user if we can not create the card"
    (let [run!-calls (atom 0)
          warn-user-of-invalid-input!-calls (atom 0)]
      (with-redefs [sut/run! #(swap! run!-calls inc)
                    sut/warn-user-of-invalid-input! #(swap! warn-user-of-invalid-input!-calls inc)]
        (let [state (r/atom {kws/card-input {kws.card/body (coercion.result/failure "" "error")}})]
          (sut/main {:state state})
          (is (= 0 @run!-calls))
          (is (= 1 @warn-user-of-invalid-input!-calls)))))))
