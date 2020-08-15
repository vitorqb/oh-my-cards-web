(ns ohmycards.web.components.form.core-test
  (:require [ohmycards.web.components.form.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

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
    (is (= [:div.simple-form__input-row {}
            [:div.simple-form__label "LABEL"]
            [:div.simple-form__input [::my-input]]]
           (sut/row {:input [::my-input] :label "LABEL"}))))

  (testing "No label"
    (is (= [:div.simple-form__input-row {}
            nil
            [:div.simple-form__input [::my-input]]]
           (sut/row {:input [::my-input]})))))
