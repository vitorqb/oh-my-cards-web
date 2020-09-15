(ns ohmycards.web.components.inputs.simple-test
  (:require [ohmycards.web.components.inputs.simple :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-gen-input-on-change-handler

  (testing "Passes the value to the outer handler"
    (let [on-change #(do [::change %])
          handler (sut/gen-input-on-change-handler on-change)]
      (is (= [::change "foo"] (handler (clj->js {:target {:value "foo"}}))))))

  (testing "Defaults class"
    (let [[_ props] (sut/main {})]
      (is (= "simple-input" (:class props)))))

  (testing "Keeps class if passed"
    (let [[_ props] (sut/main {:class "FOO"})]
      (is (= "FOO" (:class props))))))

(deftest test-input

  (testing "Wraps on-change with gen-input-on-change-handler"
    (with-redefs [sut/gen-input-on-change-handler #(do [::gen-handler %])]
      (let [[_ props] (sut/main {:on-change ::on-change})]
        (is (= (:on-change props) [::gen-handler ::on-change]))))))
