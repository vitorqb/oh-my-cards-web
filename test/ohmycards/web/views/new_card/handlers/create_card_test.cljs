(ns ohmycards.web.views.new-card.handlers.create-card-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.async-actions.core :as async-actions]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.views.new-card.handlers.create-card :as sut]
            [reagent.core :as r]))

(deftest test-create-async-action

  (let [create-card! #(do [::create-card! %1])
        action (sut/create-async-action {kws/create-card! create-card!})
        run-condition-fn (kws.async-actions/run-condition-fn action)
        pre-reducer-fn (kws.async-actions/pre-reducer-fn action)
        action-fn (kws.async-actions/action-fn action)
        post-reducer-fn (kws.async-actions/post-reducer-fn action)]

    (testing "Test run condition"
      (is (false? (run-condition-fn {kws/loading? true})))
      (is (true? (run-condition-fn {kws/loading? false}))))

    (testing "Pre reducer"
      (is (true? (kws/loading? (pre-reducer-fn {}))))
      (is (nil? (kws/error-message (pre-reducer-fn {kws/error-message "foo"})))))

    (testing "Post reducer"
      (is (false? (-> {kws/loading? true} (post-reducer-fn {}) kws/loading?)))
      (is (nil? (-> {kws/created-card ::foo} (post-reducer-fn {}) kws/created-card)))
      (is (= "errmsg"
             (kws/error-message (post-reducer-fn {} {kws.cards-crud/error-message "errmsg"}))))
      (let [card {kws.card/id "FOO"}]
        (is (= card
               (->> {kws.cards-crud/created-card card} (post-reducer-fn {}) kws/created-card)))))))

(deftest test-warn-user-of-invalid-input!

  (testing "Notifies user"
    (let [notify! #(do [::notify! %])]
      (is (= [::notify! sut/INVALID_FORM_MSG]
             (sut/warn-user-of-invalid-input! {:notify! notify!}))))))

(deftest test-main

  (testing "Warns user if we can not create the card"
    (let [run-calls (atom 0)
          warn-user-of-invalid-input!-calls (atom 0)]
      (with-redefs [async-actions/run #(swap! run-calls inc)
                    sut/warn-user-of-invalid-input! #(swap! warn-user-of-invalid-input!-calls inc)]
        (let [state (r/atom {kws/card-input {kws.card/body (coercion.result/failure "" "error")}})]
          (sut/main {:state state})
          (is (= 0 @run-calls))
          (is (= 1 @warn-user-of-invalid-input!-calls))))))
  
  (testing "Runs if input is valid"
    (let [run-calls (atom 0)
          warn-user-of-invalid-input!-calls (atom 0)]
      (with-redefs [async-actions/run #(swap! run-calls inc)
                    sut/warn-user-of-invalid-input! #(swap! warn-user-of-invalid-input!-calls inc)]
        (let [state (r/atom {kws/card-input {kws.card/body (coercion.result/success "" "")}})]
          (sut/main {:state state})
          (is (= 1 @run-calls))
          (is (= 0 @warn-user-of-invalid-input!-calls)))))))
