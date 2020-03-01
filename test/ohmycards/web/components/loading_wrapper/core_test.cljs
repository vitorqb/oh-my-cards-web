(ns ohmycards.web.components.loading-wrapper.core-test
  (:require [ohmycards.web.components.loading-wrapper.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-main

  (testing "Renders only child if not loadin"
    (is (= [:<> '([:div "foo"])]
           (sut/main {:loading? false} [:div "foo"]))))

  (testing "Renders loading view if loading"
    (is (= [:<> [sut/loading-view] '([:div "foo"])]
           (sut/main {:loading? true} [:div "foo"])))))
