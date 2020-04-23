(ns ohmycards.web.views.edit-card.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.good-message-box.core :as good-message-box]
            [ohmycards.web.components.inputs.markdown :as inputs.markdown]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.edit-card.core :as sut]))

(deftest test-header

  (testing "Has a go-home btn"
    (let [props {::foo 1}]
      (is
       (some
        #(= [sut/go-home-btn props] %)
        (tu/comp-seq (sut/header props))))))

  (testing "Has a remove-btn"
    (let [props {::foo 1}]
      (is
       (some
        #(= [sut/remove-btn props] %)
        (tu/comp-seq (sut/header props))))))

  (testing "Has a update-btn"
    (let [props {::foo 1}]
      (is
       (some
        #(= [sut/update-btn props] %)
        (tu/comp-seq (sut/header props)))))))

(deftest test-id-input-row
  (let [state (atom {kws/card-input {kws.card/id "FOO"}})]
    (is
     (some
      #(= [form.input/main {:disabled true :value "FOO"}] %)
      (tu/comp-seq (sut/id-input-row {:state state}))))))

(deftest test-title-input-row
  (let [state     (atom {kws/card-input {kws.card/title "FOO"}})
        comp      (sut/title-input-row {:state state})
        [_ props] (tu/get-first
                   #(= (tu/safe-first %) form.input/main)
                   (tu/comp-seq comp))]
    (is (= "FOO" (:value props)))
    (is (ifn? (:on-change props)))))

(deftest test-body-input-row
  (let [state     (atom {kws/card-input {kws.card/body "FOO"}})
        comp      (sut/body-input-row {:state state})
        props     (tu/get-props-for inputs.markdown/main (tu/comp-seq comp))]
    (is (= "FOO" (:value props)))
    (is (ifn? (:on-change props)))))

(deftest test-tags-input-row
  (testing "Renders a tags input"
    (with-redefs [form.input/build-props #(do {:state %1 :path %2})]
      (is
       (tu/exists-in-component?
        [inputs.tags/main {:state ::state :path [kws/card-input kws.card/tags] }]
        (sut/tags-input-row {:state ::state}))))))

(deftest test-form

  (testing "Renders row for title"
    (is (tu/exists-in-component? [sut/title-input-row {}] (sut/form {}))))

  (testing "Renders row for Id"
    (is (tu/exists-in-component? [sut/id-input-row {}] (sut/form {}))))

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
