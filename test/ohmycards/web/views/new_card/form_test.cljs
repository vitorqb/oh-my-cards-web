(ns ohmycards.web.views.new-card.form-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.new-card.form :as sut]))

(deftest test-title-input

  (testing "On change mutates state"
    (let [state (atom {})
          comp  (sut/title-input {:state state})
          [_ {:keys [on-change]}] (tu/get-first #(= (tu/safe-first %) form.input/main) comp)]
      (on-change "foo")
      (is (= @state {kws/card-input {kws.card/title "foo"}}))))

  (testing "Passes value"
    (let [state (atom {kws/card-input {kws.card/title "Foo"}})
          comp  (sut/title-input {:state state})
          [_ {:keys [value]}] (tu/get-first #(= (tu/safe-first %) form.input/main) comp)]
      (is (= value "Foo")))))

(deftest test-body-input

  (testing "On change mutates state"
    (let [state (atom {})
          comp  (sut/body-input {:state state})
          [_ {:keys [on-change]}] (tu/get-first #(= (tu/safe-first %) form.input/main) comp)]
      (on-change "foo")
      (is (= @state {kws/card-input {kws.card/body "foo"}}))))

  (testing "Passes value"
    (let [state (atom {kws/card-input {kws.card/body "Foo"}})
          comp  (sut/body-input {:state state})
          [_ {:keys [value]}] (tu/get-first #(= (tu/safe-first %) form.input/main) comp)]
      (is (= value "Foo")))))

(deftest test-tags-input

  (let [props {:state (atom {})}]

    (testing "Renders label"
      (is (tu/exists-in-component?
           [:span.new-card-form__label "Tags"]
           (sut/tags-input props))))

    (testing "Renders a tags-input"
      (let [card {kws.card/tags ["A"]}
            comp (sut/tags-input (assoc props :state (atom {kws/card-input card})))
            [_ tag-input-props] (tu/get-first #(= (tu/safe-first %) inputs.tags/main)
                                              (tu/comp-seq comp))
            {:keys [value on-change]} tag-input-props]
        (is (= ["A"] value))
        (is (ifn? on-change))
        (is (= (on-change ["A" "B"]) {kws/card-input {kws.card/tags ["A" "B"]}}))))))

(deftest test-main

  (let [props {kws/goto-home! (constantly ::foo)}]

    (testing "Renders a title input"
      (is (tu/exists-in-component? [sut/title-input props] (sut/main props))))

    (testing "Renders a body input"
      (is (tu/exists-in-component? [sut/body-input props] (sut/main props))))

    (testing "Renders a tags input"
      (is (tu/exists-in-component? [sut/tags-input props] (sut/main props))))))
