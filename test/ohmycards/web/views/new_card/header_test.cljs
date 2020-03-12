(ns ohmycards.web.views.new-card.header-test
  (:require  [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
             [ohmycards.web.icons :as icons]
             [ohmycards.web.views.new-card.header :as sut]
             [ohmycards.web.kws.views.new-card.core :as kws]
             [ohmycards.web.test-utils :as tu]))

(deftest test-header-left

  (testing "Passes goto-home to button"
    (let [props {kws/goto-home! (constantly ::foo)}
          [_ btn-props] (tu/get-first
                         #(= (tu/safe-first %) :button.clear-button)
                         (tu/comp-seq (sut/header-left props)))]
      (is (= ::foo ((:on-click btn-props)))))))

(deftest test-main
  (is (= [:div.new-card-header
          [sut/header-left {}]
          [sut/header-center {}]
          [:div.new-card-header__right]]
         (sut/main {}))))
