(ns ohmycards.web.views.cards-grid.config-dashboard.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.cards-grid.config-dashboard.core :as sut]))

(deftest test-main

  (testing "Contains header"
    (let [props {::foo 1}]
      (is (some #(= [sut/header props] %1)
                (tu/comp-seq (sut/main props)))))))

(deftest test-page-config

  (let [get-state #(atom (apply hash-map kws/page 2 %&))]

    (testing "Contains input with value"
      (let [props {:state (get-state)}
            [_ input-props] (tu/get-first #(= (tu/safe-first %) form.input/main)
                                          (sut/page-config props))]
        (is (= (:value input-props) 2))))))

(deftest test-page-site-config

  (let [get-state #(atom (apply hash-map kws/page-size 20 %&))]

    (testing "Contains input with value"
      (let [props {:state (get-state)}
            [_ input-props] (tu/get-first #(= (tu/safe-first %) form.input/main)
                                          (sut/page-size-config props))]
        (is (= (:value input-props) 20))))))
