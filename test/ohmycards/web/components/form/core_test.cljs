(ns ohmycards.web.components.form.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as sut]))

(deftest test-main

  (testing "Passes submit-handler using gen-submit-handler"
    (with-redefs [sut/gen-submit-handler #(do [::handler %])]
      (let [[_ props] (sut/main {::sut/on-submit ::on-submit})]
        (is (= (:on-submit props) [::handler ::on-submit]))))))

(deftest test-gen-submit-handler

  (testing "Calls preventDefault."
    (let [prevent-default-called? (atom false)
          event (clj->js {:preventDefault #(reset! prevent-default-called? true)})
          handler (sut/gen-submit-handler identity)]
      (handler event)
      (is (true? @prevent-default-called?))))

  (testing "Calls onSubmit"
    (let [event (clj->js {:preventDefault #(do)})
          handler (sut/gen-submit-handler #(do ::result))]
      (is (= ::result (handler event))))))

(deftest test-row

  (testing "Base"
    (is (= [:div.simple-form__row {}
            [:div.simple-form__label "LABEL"]
            [:div.simple-input [::my-input]]
            [error-message-box/main {:value "ERROR"}]]
           (sut/row {:input [::my-input] :label "LABEL" :error-message "ERROR"}))))

  (testing "No label"
    (is (= [:div.simple-form__row {}
            nil
            [:div.simple-input [::my-input]]
            [error-message-box/main {:value nil}]]
           (sut/row {:input [::my-input]})))))
