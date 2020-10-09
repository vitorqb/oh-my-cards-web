(ns ohmycards.web.views.cards-grid.control-header-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.views.cards-grid.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.cards-grid.control-header :as sut]
            [ohmycards.web.views.cards-grid.state-management :as state-management]))

(deftest test-page-counter
  (is (= [:span.small-box "2 | 3"]
         (sut/page-counter {::sut/page 2 ::sut/max-page 3}))))

(deftest test-header-left
  (let [props {kws/goto-newcard! 1}]
    (is (= [:<>
            [sut/new-card-btn 1]
            [sut/refresh-btn props]
            [sut/filter-btn props]]
           (sut/header-left props)))))

(deftest test-refresh-btn

  (testing "Calls refetch!"
    (let [props {::foo 1}]
      (with-redefs [state-management/refetch! #(if (= % props) ::result)]
        (let [comp (sut/refresh-btn props)]
          (is (= ((get-in comp [1 :on-click])) ::result)))))))

(deftest test-filter-btn

  (testing "Calls toggle-filter!"
    (let [props {::foo 1}]
      (with-redefs [state-management/toggle-filter! #(if (= % props) ::result)]
        (let [comp (sut/filter-btn props)]
          (is (= ::result ((get-in comp [1 :on-click])))))))))

(deftest test-header-center

  (testing "Has a page counter"
    (is
     (some
      #(= [sut/page-counter {::sut/page 2 ::sut/max-page 2}] %)
      (sut/header-center
       {:state (atom {kws/config {kws.config/page 2 kws.config/page-size 1}
                      kws/count-of-cards 2})})))))

(deftest test-main

  (testing "Renders a header-left"
    (let [props {:state (atom {})}]
      (is
       (some
        #(= [sut/header-left props] %)
        (tu/comp-seq (sut/main props))))))

  (testing "Renders a header-center"
    (let [props {:state (atom {})}]
      (is
       (some
        #(= [sut/header-center props] %)
        (tu/comp-seq (sut/main props))))))

  (testing "Renders a header-right"
    (let [props {:state (atom {})}]
      (is
       (some
        #(= [sut/header-right props] %)
        (tu/comp-seq (sut/main props)))))))
