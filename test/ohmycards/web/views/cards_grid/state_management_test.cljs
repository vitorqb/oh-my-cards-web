(ns ohmycards.web.views.cards-grid.state-management-test
  (:require [ohmycards.web.views.cards-grid.state-management :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [ohmycards.web.kws.services.fetch-cards.core :as kws.fetch-cards]))

(deftest test-reduce-on-fetched-cards

  (testing "On Success"

    (let [state {::foo 1 kws.cards-grid/error-message nil}
          fetch-res {kws.fetch-cards/cards [{:id "1" :title "FOO" :body "foo bar baz"}]
                     kws.fetch-cards/page 2
                     kws.fetch-cards/page-size 10}
          new-state (sut/reduce-on-fetched-cards state fetch-res)]

      (testing "Unsets error message"
        (is (nil? (kws.cards-grid/error-message new-state))))

      (testing "Set's cards"
        (is (= [{:id "1" :title "FOO" :body "foo bar baz"}]
               (kws.cards-grid/cards new-state))))

      (testing "Sets the state to ready"
        (is (= kws.cards-grid/status-ready (kws.cards-grid/status new-state))))

      (testing "Sets the current page"
        (is (= 2 (kws.cards-grid/page new-state))))

      (testing "Sets the current page size"
        (is (= 10 (kws.cards-grid/page-size new-state)))))))

(deftest test-has-next-page?
  (is (true? (sut/has-next-page? {::kws.cards-grid/count-of-cards 10
                                  ::kws.cards-grid/page 4
                                  ::kws.cards-grid/page-size 2})))
  (is (false? (sut/has-next-page? {::kws.cards-grid/count-of-cards 10
                                   ::kws.cards-grid/page 5
                                   ::kws.cards-grid/page-size 2})))
  (is (true? (sut/has-next-page? {::kws.cards-grid/count-of-cards 11
                                   ::kws.cards-grid/page 5
                                   ::kws.cards-grid/page-size 2}))))

(deftest test-fetch-cards-params

  (testing "Base"
    (is (= {kws.fetch-cards/page 1 kws.fetch-cards/page-size 2}
           (sut/fetch-cards-params {kws.cards-grid/page 1 kws.cards-grid/page-size 2})))))

(deftest test-state-not-initialized?

  (testing "nil -> false"
    (is (false? (sut/state-initialized? {}))))

  (testing "::ready -> true"
    (is (true? (sut/state-initialized? {kws.cards-grid/status kws.cards-grid/status-ready})))))

(deftest test-refetch-from-props!
  (with-redefs [sut/refetch! #(do [%1 %2])]
    (is (= [1 2] (sut/refetch-from-props! {:state 1 kws.cards-grid/fetch-cards! 2})))))
