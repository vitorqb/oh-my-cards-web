(ns ohmycards.web.views.new-card.form-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.inputs.markdown :as inputs.markdown]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.components.inputs.tags :as kws.inputs.tags]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.new-card.form :as sut]))

(deftest test-title-input

  (letfn [(gen-comp [state] (sut/title-input {:state state}))
          (find-input [comp] (->> comp tu/comp-seq (tu/get-props-for form/row) :input))
          (get-input-props [state] (-> state gen-comp find-input second))]

    (testing "On change mutates state"
      (let [state       (atom {})
            input-props (get-input-props state)
            on-change   (:on-change input-props)]
        (on-change "foo")
        (is (= {kws/card-input {kws.card/title "foo"}} @state))))

    (testing "Passes value"
      (let [state       (atom {kws/card-input {kws.card/title "Foo"}})
            input-props (get-input-props state)]
        (is (= "Foo" (:value input-props)))))))

(deftest test-body-input

  (letfn [(gen-comp [state] (sut/body-input {:state state}))
          (get-props [comp] (tu/get-props-for inputs.markdown/main (tu/comp-seq comp)))]

    (testing "On change mutates state"
      (let [state (atom {})
            comp  (gen-comp state)
            props (get-props comp)]
        ((:on-change props) "foo")
        (is (= {kws/card-input {kws.card/body "foo"}} @state))))

    (testing "Passes value"
      (let [state (atom {kws/card-input {kws.card/body "Foo"}})
            comp  (gen-comp state)
            props (get-props comp)]
        (is (= (:value props) "Foo"))))))

(deftest test-tags-input

  (let [props {:state (atom {})
               kws/cards-metadata {kws.card-metadata/tags ["A"]}}]

    (testing "Renders label"
      (let [label (->> props sut/tags-input tu/comp-seq (tu/get-props-for form/row) :label)]
        (is (= "Tags" label))))

    (testing "Renders a tags-input"
      (let [card {kws.card/tags ["A"]}
            comp (sut/tags-input (assoc props :state (atom {kws/card-input card})))
            tag-input-props (tu/get-props-for inputs.tags/main (tu/comp-seq comp))
            {:keys [value on-change]} tag-input-props]

        (testing "With value"
          (is (= ["A"] value)))

        (testing "With function for on-change"
          (is (ifn? on-change))
          (is (= (on-change ["A" "B"]) {kws/card-input {kws.card/tags ["A" "B"]}})))

        (testing "With all-tags"
          (is (= ["A"] (kws.inputs.tags/all-tags tag-input-props))))))))

(deftest test-main

  (let [props {kws/goto-home! (constantly ::foo)}]

    (testing "Renders a title input"
      (is (tu/exists-in-component? [sut/title-input props] (sut/main props))))

    (testing "Renders a body input"
      (is (tu/exists-in-component? [sut/body-input props] (sut/main props))))

    (testing "Renders a tags input"
      (is (tu/exists-in-component? [sut/tags-input props] (sut/main props))))))
