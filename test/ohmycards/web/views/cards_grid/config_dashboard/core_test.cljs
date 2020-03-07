(ns ohmycards.web.views.cards-grid.config-dashboard.core-test
  (:require [ohmycards.web.views.cards-grid.config-dashboard.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.test-utils :as tu]))

(deftest test-main

  (testing "Contains header"
    (let [props {::foo 1}]
      (is (some #(= [sut/header props] %1)
                (tu/comp-seq (sut/main props)))))))
