(ns ohmycards.web.views.edit-card.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.good-message-box.core :as good-message-box]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.components.inputs.markdown :as inputs.markdown]
            [ohmycards.web.components.inputs.simple :as inputs.simple]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.components.inputs.tags :as kws.inputs.tags]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.edit-card.core :as sut]
            [reagent.core :as r]))

(deftest test-display-btn
  (testing "Calls `goto-displaycard!` with id"
    (let [goto-displaycard! #(do [::result %])
          state (atom {kws/card-input {kws.card/id 1}})
          props {:state state kws/goto-displaycard! goto-displaycard!}
          on-click (-> props sut/display-btn second :on-click)]
      (is (= (on-click 1) [::result 1])))))

(deftest test-header

  (let [props {::foo 1}
        comp (sut/header props)
        comp-seq (tu/comp-seq comp)]

    (testing "Has a go-home btn"
      (is (some #(= [sut/go-home-btn props] %) comp-seq)))

    (testing "Has a remove-btn"
      (is (some #(= [sut/remove-btn props] %) comp-seq)))

    (testing "Has a update-btn"
      (is (some #(= [sut/update-btn props] %) comp-seq)))

    (testing "Has a display-btn"
      (is (some #(= [sut/display-btn props] %) comp-seq)))))

(deftest test-id-input-row
  (with-redefs [r/cursor #(do [::cursor %1 %2])]
    (let [state (r/atom {kws/card-input {kws.card/id "FOO"}})]
      (is (= [form/row
              {:label "Id"
               :input [inputs/main
                       {kws.inputs/cursor [::cursor state [kws/card-input kws.card/id]]
                        kws.inputs/disabled? true}]}]
             (sut/id-input-row {:state state}))))))

(deftest test-ref-input-row
  (with-redefs [r/cursor #(do [::cursor %1 %2])]
    (let [state (atom {kws/card-input {kws.card/ref 1}})]
      (is (= [form/row
              {:label "Ref"
               :input [inputs/main
                       {kws.inputs/cursor [::cursor state [kws/card-input kws.card/ref]]
                        kws.inputs/disabled? true}]}]
             (sut/ref-input-row {:state state}))))))

(deftest test-title-input-row
  (with-redefs [r/cursor #(do [::cursor %1 %2])]
    (let [state (atom {kws/card-input {kws.card/title "FOO"}})]
      (is (= [form/row
              {:label "Title"
               :input [inputs/main
                       {kws.inputs/cursor [::cursor state [kws/card-input kws.card/title]]}]}]
             (sut/title-input-row {:state state}))))))

(deftest test-body-input-row
  (with-redefs [r/cursor #(do [::cursor %1 %2])]
    (let [state (atom {kws/card-input {kws.card/body "FOO"}})]
      (is (= [form/row
              {:label "Body"
               :input [inputs/main
                       {kws.inputs/cursor [::cursor state [kws/card-input kws.card/body]]
                        kws.inputs/itype kws.inputs/t-markdown}]}]
             (sut/body-input-row {:state state}))))))

(deftest test-tags-input-row

  (testing "Renders a tags input"
    (with-redefs [r/cursor #(do [::cursor %1 %2])]
      (let [state (atom {})
            props {:state state kws/cards-metadata {kws.card-metadata/tags ["A"]}}]
        (is (= [form/row
                {:label "Tags"
                 :input [inputs/main
                         {kws.inputs/cursor [::cursor state [kws/card-input kws.card/tags]]
                          kws.inputs/itype kws.inputs/t-tags
                          kws.inputs/props {kws.inputs.tags/all-tags ["A"]}}]}]
               (sut/tags-input-row props)))))))

(deftest test-form

  (testing "Renders row for title"
    (is (tu/exists-in-component? [sut/title-input-row {}] (sut/form {}))))

  (testing "Renders row for Id"
    (is (tu/exists-in-component? [sut/id-input-row {}] (sut/form {}))))

  (testing "Renders row for Ref"
    (is (tu/exists-in-component? [sut/ref-input-row {}] (sut/form {}))))

  (testing "Renders row for body"
    (is (tu/exists-in-component? [sut/body-input-row {}] (sut/form {}))))

  (testing "Renders row for tags"
    (is (tu/exists-in-component? [sut/tags-input-row {}] (sut/form {})))))

(deftest test-main

  (testing "Renders header"
    (let [props {:state (atom nil)}]
      (is
       (some
        #(= [sut/header props] %)
        (tu/comp-seq (sut/main props))))))

  (testing "Renders form"
    (let [props {:state (atom nil)}]
      (is
       (some
        #(= [sut/form props] %)
        (tu/comp-seq (sut/main props))))))

  (testing "Renders a loading wrapper"
    (let [props          {:state (atom {kws/loading? true})}
          comp           (sut/main props)
          [_ wrap_props] (tu/get-first #(= (tu/safe-first %) loading-wrapper/main)
                                       (tu/comp-seq comp))]
      (is (= {:loading? true} wrap_props))))

  (testing "Renders error message"
    (let [props {:state (atom {kws/error-message "ERR"})}
          comp (sut/main props)]
      (is (some #(= % [error-message-box/main {:value "ERR"}]) (tu/comp-seq comp)))))

  (testing "Renders good message"
    (let [props {:state (atom {kws/good-message "YEY!"})}
          comp (sut/main props)]
      (is (some #(= % [good-message-box/main {:value "YEY!"}]) (tu/comp-seq comp))))))
