(ns ohmycards.web.views.cards-grid.state-management-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.services.fetch-cards.core :as kws.fetch-cards]
            [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [ohmycards.web.views.cards-grid.state-management :as sut]))

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

(deftest test-set-config-profile
  (let [profile {kws.profile/name "FOO"
                 kws.profile/config {kws.config/exclude-tags ["A"]
                                     kws.config/include-tags ["B"]
                                     kws.config/page 1
                                     kws.config/page-size 2
                                     kws.config/tags-filter-query "(FOO)"}}]
    (is (= {kws.cards-grid/exclude-tags ["A"]
            kws.cards-grid/include-tags ["B"]
            kws.cards-grid/page 1
            kws.cards-grid/page-size 2
            kws.cards-grid/tags-filter-query "(FOO)"}
           (sut/set-config-profile {} profile)))))

(deftest test-fetch-cards-params

  (let [source-params {kws.cards-grid/page 1
                       kws.cards-grid/page-size 2
                       kws.cards-grid/include-tags ["A"]
                       kws.cards-grid/exclude-tags ["C"]
                       kws.cards-grid/tags-filter-query "(tags CONTAINS 'foo')"}
        expected-params {kws.fetch-cards/page 1
                         kws.fetch-cards/page-size 2
                         kws.fetch-cards/include-tags ["A"]
                         kws.fetch-cards/exclude-tags ["C"]
                         kws.fetch-cards/tags-filter-query "(tags CONTAINS 'foo')"}]

    (testing "Base"
      (is (= expected-params (sut/fetch-cards-params source-params))))

    (testing "Without tags query filter"
      (let [source-params   (dissoc source-params kws.cards-grid/tags-filter-query)
            expected-params (dissoc expected-params kws.fetch-cards/tags-filter-query)]
        (is (= expected-params (sut/fetch-cards-params source-params)))))))

(deftest test-state-not-initialized?

  (testing "nil -> false"
    (is (false? (sut/state-initialized? {}))))

  (testing "::ready -> true"
    (is (true? (sut/state-initialized? {kws.cards-grid/status kws.cards-grid/status-ready})))))

(deftest test-refetch-from-props!
  (with-redefs [sut/refetch! #(do [%1 %2])]
    (is (= [1 2] (sut/refetch-from-props! {:state 1 kws.cards-grid/fetch-cards! 2})))))
