(ns ohmycards.web.views.new-card.header-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.header.core :as header]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.new-card.header :as sut]))

(deftest test-header-left

  (testing "Passes goto-home to button"
    (let [props {kws/goto-home! (constantly ::foo)}
          [_ btn-props] (tu/get-first
                         #(= (tu/safe-first %) :button.icon-button)
                         (tu/comp-seq (sut/header-left props)))]
      (is (= ::foo ((:on-click btn-props)))))))

(deftest test-main
  (is (= [header/main {:left   [sut/header-left {}]
                       :center [sut/header-center {}]}]
         (sut/main {}))))
