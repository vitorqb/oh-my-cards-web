(ns ohmycards.web.views.cards-grid.control-header-test
  (:require [ohmycards.web.views.cards-grid.control-header :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.views.cards-grid.core :as kws]
            [ohmycards.web.test-utils :as tu]))

(deftest test-page-counter
  (is (= [:span.small-box "2 | 3"]
         (sut/page-counter {::sut/page 2 ::sut/max-page 3}))))

(deftest test-header-left
  (let [props {kws/goto-newcard! 1}]
    (is (= [:span.cards-grid-header__left [sut/new-card-btn 1]]
           (sut/header-left props)))))

(deftest test-header-center

  (testing "Has a page counter"
    (is
     (some
      #(= [sut/page-counter {::sut/page 2 ::sut/max-page 2}] %)
      (sut/header-center
       {:state (atom {kws/page 2 kws/count-of-cards 2 kws/page-size 1})})))))

(deftest test-main

  (testing "Renders a header-left"
    (let [props {:state (atom {})}]
      (is
       (some
        #(= [sut/header-left props] %)
        (sut/main props)))))

  (testing "Renders a header-center"
    (let [props {:state (atom {})}]
      (is
       (some
        #(= [sut/header-center props] %)
        (sut/main props)))))

  (testing "Renders a header-right"
    (let [props {:state (atom {})}]
      (is
       (some
        #(= [sut/header-right props] %)
        (sut/main props))))))
