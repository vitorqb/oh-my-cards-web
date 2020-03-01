(ns ohmycards.web.views.login.email-row-test
  (:require [ohmycards.web.views.login.email-row :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-props-builder
  (let [state {:email "VALUE" :onetime-password-sent? false}]

    (testing "Disabled if onetimepassword has been sent"
      (let [state* (assoc state :onetime-password-sent? true)
            props {:state (atom state*)}]
        (is (= true (-> props sut/props-builder :disabled?)))))

    (testing "Passes value"
      (let [props {:state (atom state)}]
        (is (= "VALUE" (-> props sut/props-builder :value)))))

    (testing "onChange mutates atom"
      (let [state* (atom state)
            props (sut/props-builder {:state state*})]
        ((:on-change props) "NEW")
        (is (= "NEW" (:email @state*)))))))
