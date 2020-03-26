(ns ohmycards.web.views.cards-grid.config-dashboard.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.cards-grid.config-dashboard.core :as sut]))

(deftest test-main

  (testing "Contains header"
    (let [props {::foo 1}]
      (is (tu/exists-in-component? [sut/header props] (sut/main props)))))

  (testing "Contains include-tags config"
    (let [props {::foo 1}]
      (is (tu/exists-in-component? [sut/include-tags-config props] (sut/main props))))))

(deftest test-page-config

  (let [get-state #(atom (apply hash-map kws/page 2 %&))]

    (testing "Contains input with value"
      (let [props {:state (get-state)}
            [_ input-props] (tu/get-first #(= (tu/safe-first %) form.input/main)
                                          (tu/comp-seq (sut/page-config props)))]
        (is (= (:value input-props) 2))))))

(deftest test-page-site-config

  (let [get-state #(atom (apply hash-map kws/page-size 20 %&))]

    (testing "Contains input with value"
      (let [props {:state (get-state)}
            [_ input-props] (tu/get-first #(= (tu/safe-first %) form.input/main)
                                          (tu/comp-seq (sut/page-size-config props)))]
        (is (= (:value input-props) 20))))))

(deftest test-include-tags-config

  (let [get-state #(atom (apply hash-map kws/include-tags ["A"] %&))]

    (testing "Includes label"
      (let [props {:state (get-state)}]
        (is (tu/exists-in-component? (sut/label "ALL tags") (sut/include-tags-config props)))))

    (testing "Contains input with value"
      (let [props {:state (get-state)}
            comp (sut/include-tags-config props)
            [_ input-props] (tu/get-first #(= (tu/safe-first %) inputs.tags/main)
                                          (tu/comp-seq comp))]
        (is (= (:value input-props) ["A"]))))))
