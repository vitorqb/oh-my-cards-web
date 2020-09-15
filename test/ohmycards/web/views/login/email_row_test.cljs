(ns ohmycards.web.views.login.email-row-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.login.email-row :as sut]
            [reagent.core :as r]))

(deftest test-main
  (let [state {:email "VALUE" :onetime-password-sent? false}]

    (testing "Disabled if onetimepassword has been sent"
      (let [state* (assoc state :onetime-password-sent? true)
            props {:state (r/atom state*)}
            comp (sut/main props)
            input-props (tu/get-props-for inputs/main (tu/comp-seq comp))]
        (is (= true (kws.inputs/disabled? input-props)))))

    (testing "Passes value"
      (let [props {:state (r/atom state)}
            comp (sut/main props)
            input-props (tu/get-props-for inputs/main (tu/comp-seq comp))]
        (is (= "VALUE" @(kws.inputs/cursor input-props)))))

    (testing "onChange mutates r/atom"
      (let [state* (r/atom state)
            comp (sut/main {:state state*})
            input-props (tu/get-props-for inputs/main (tu/comp-seq comp))]
        (reset! (kws.inputs/cursor input-props) "NEW")
        (is (= "NEW" (:email @state*)))))))
