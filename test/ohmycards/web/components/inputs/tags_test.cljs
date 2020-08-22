(ns ohmycards.web.components.inputs.tags-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.inputs.combobox :as inputs.combobox]
            [ohmycards.web.components.inputs.tags :as sut]
            [ohmycards.web.kws.components.inputs.combobox.core
             :as
             kws.inputs.combobox]
            [ohmycards.web.kws.components.inputs.combobox.options
             :as
             kws.inputs.combobox.options]
            [ohmycards.web.kws.components.inputs.tags :as kws]
            [ohmycards.web.test-utils :as tu]))

(deftest test-zip-tags
  (is (= [[nil 0 :append]] (sut/zip-tags [])))
  (is (= [["A" 0 0] [nil 1 :append]] (sut/zip-tags ["A"])))
  (is (= [["A" 0 0] ["B" 1 1] [nil 2 :append]] (sut/zip-tags ["A" "B"]))))

(deftest tag-change-handler

  (testing "Changing at index 0"
    (let [tags ["A" "B"]
          props {:value tags :on-change #(do [::change %1])}
          handler (sut/tag-change-handler props 0)]
      (is (= [::change ["C" "B"]] (handler "C")))))

  (testing "Changing at index 0 when value is nil"
    (let [props {:value nil :on-change #(do [::change %1])}
          handler (sut/tag-change-handler props :append)]
      (is (= [::change ["C"]] (handler "C")))))

  (testing "Changing at index 1"
    (let [tags ["A" "B"]
          props {:value tags :on-change #(do [::change %1])}
          handler (sut/tag-change-handler props 1)]
      (is (= [::change ["A" "C"]] (handler "C")))))

  (testing "Changing at index ::append"
    (let [tags ["A" "B"]
          props {:value tags :on-change #(do [::change %1])}
          handler (sut/tag-change-handler props :append)]
      (is (= [::change ["A" "B" "C"]] (handler "C"))))))

(deftest test-single-tag-input

  (testing "Base"
    (is
     (= [inputs.combobox/main
         {:class "tags-input__input"
          :type "text"
          :value nil
          :on-change ::on-change
          kws.inputs.combobox/options [{kws.inputs.combobox.options/value "A"}]}]
        (sut/single-tag-input {:on-change ::on-change :all-tags ["A"]})))))

(deftest test-main

  (testing "Renders a single-tag-input for each tag"
    (with-redefs [sut/tag-change-handler #(do [::change %1 %2])]
      (let [props {:value ["TAG"] kws/all-tags ["A"]}]
        (is
         (tu/exists-in-component?
          [sut/single-tag-input {:key 0
                                 :value "TAG"
                                 :on-change [::change props 0]
                                 :all-tags ["A"]}]
          (sut/main props))))))

  (testing "Renders a single-tag-input with append"
    (with-redefs [sut/tag-change-handler #(do [::change %1 %2])]
      (is
       (tu/exists-in-component?
        [sut/single-tag-input {:key 0
                               :value nil
                               :on-change [::change {} :append]
                               :all-tags nil}]
        (sut/main {}))))))

(deftest get-class
  (is (= "tags-input" (sut/get-class nil)))
  (is (= "foo" (sut/get-class "foo"))))
