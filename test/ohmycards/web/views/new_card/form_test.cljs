(ns ohmycards.web.views.new-card.form-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.components.inputs.markdown :as inputs.markdown]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.components.inputs.tags :as kws.inputs.tags]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.new-card.form :as sut]
            [reagent.core :as r]))

(deftest test-title-input
  (with-redefs [r/cursor #(do [::cursor %1 %2])]
    (let [state (atom {})]
      (is (= [form/row
              {:label "Title"
               :input [inputs/main
                       {kws.inputs/cursor [::cursor state [kws/card-input kws.card/title]]
                        kws.inputs/props {:auto-focus true}
                        kws.inputs/coercer identity}]}]
             (sut/title-input {:state state}))))))

(deftest test-body-input
  (with-redefs [r/cursor #(do [::cursor %1 %2])]
    (let [state (atom {})]
      (is (= [form/row
              {:label "Body"
               :input [inputs/main
                       {kws.inputs/cursor [::cursor state [kws/card-input kws.card/body]]
                        kws.inputs/itype kws.inputs/t-markdown
                        kws.inputs/coercer identity}]}]
             (sut/body-input {:state state}))))))

(deftest test-tags-input
  (let [state (r/atom {})
        props {:state state kws/cards-metadata {kws.card-metadata/tags ["A"]}}]
    (is (= [form/row
            {:label "Tags"
             :input [inputs/main
                     {kws.inputs/cursor (r/cursor state [kws/card-input kws.card/tags])
                      kws.inputs/itype kws.inputs/t-tags
                      kws.inputs/props {kws.inputs.tags/all-tags ["A"]}
                      kws.inputs/coercer coercers/tags}]
             :error-message nil}]
           (sut/tags-input props)))))

(deftest test-main

  (let [props {kws/goto-home! (constantly ::foo)}]

    (testing "Renders a title input"
      (is (tu/exists-in-component? [sut/title-input props] (sut/main props))))

    (testing "Renders a body input"
      (is (tu/exists-in-component? [sut/body-input props] (sut/main props))))

    (testing "Renders a tags input"
      (is (tu/exists-in-component? [sut/tags-input props] (sut/main props))))))
