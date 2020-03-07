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

(deftest test-gen-input-on-change-handler

  (testing "Passes the value to the outer handler"
    (let [on-change #(do [::change %])
          handler (sut/gen-input-on-change-handler on-change)]
      (is (= [::change "foo"] (handler (clj->js {:target {:value "foo"}}))))))

  (testing "Defaults class"
    (let [[_ props] (sut/input {})]
      (is (= "simple-form__input" (:class props)))))

  (testing "Keeps class if passed"
    (let [[_ props] (sut/input {:class "FOO"})]
      (is (= "FOO" (:class props))))))

(deftest test-input

  (testing "Wraps on-change with gen-input-on-change-handler"
    (with-redefs [sut/gen-input-on-change-handler #(do [::gen-handler %])]
      (let [[_ props] (sut/input {:on-change ::on-change})]
        (is (= (:on-change props) [::gen-handler ::on-change]))))))
