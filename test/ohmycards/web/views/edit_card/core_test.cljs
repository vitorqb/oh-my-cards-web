(ns ohmycards.web.views.edit-card.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.good-message-box.core :as good-message-box]
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
      #(= [form/input {:disabled true :value "FOO"}] %)
      (tu/comp-seq (sut/id-input-row {:state state}))))))

(deftest test-title-input-row
  (let [state     (atom {kws/card-input {kws.card/title "FOO"}})
        comp      (sut/title-input-row {:state state})
        [_ props] (tu/get-first
                   #(= (tu/safe-first %) form/input)
                   (tu/comp-seq comp))]
    (is (= "FOO" (:value props)))
    (is (ifn? (:on-change props)))))

(deftest test-body-input-row
  (let [state     (atom {kws/card-input {kws.card/body "FOO"}})
        comp      (sut/body-input-row {:state state})
        [_ props] (tu/get-first
                   #(= (tu/safe-first %) form/input)
                   (tu/comp-seq comp))]
    (is (= "FOO" (:value props)))
    (is (ifn? (:on-change props)))))

(deftest test-form

  (testing "Renders row for title"
    (is
     (some
      #(= [sut/title-input-row {}] %)
      (tu/comp-seq (sut/form {})))))

  (testing "Renders row for Id"
    (is
     (some
      #(= [sut/id-input-row {}] %)
      (tu/comp-seq (sut/form {})))))

  (testing "Renders row for body"
    (is
     (some
      #(= [sut/body-input-row {}] %)
      (tu/comp-seq (sut/form {}))))))

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
