(ns ohmycards.web.views.cards-grid.state-management-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.services.fetch-cards.core :as kws.fetch-cards]
            [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [ohmycards.web.views.cards-grid.state-management :as sut]))

(deftest test-reduce-on-fetched-cards

  (testing "On Success"

    (let [state {::foo 1
                 kws.cards-grid/error-message nil
                 kws.cards-grid/config {kws.config/tags-filter-query "(FOO)"}}
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
        (is (= 2 (-> new-state kws.cards-grid/config kws.config/page))))

      (testing "Sets the current page size"
        (is (= 10 (-> new-state kws.cards-grid/config kws.config/page-size))))

      (testing "Preserves old config"
        (is (= "(FOO)" (-> new-state kws.cards-grid/config kws.config/tags-filter-query)))))))

(deftest test-has-next-page?
  (is (true? (sut/has-next-page? {kws.cards-grid/count-of-cards 10
                                  kws.cards-grid/config {kws.config/page 4
                                                         kws.config/page-size 2}})))
  (is (false? (sut/has-next-page? {kws.cards-grid/count-of-cards 10
                                   kws.cards-grid/config {kws.config/page 5
                                                          kws.config/page-size 2}})))
  (is (true? (sut/has-next-page? {kws.cards-grid/count-of-cards 11
                                  kws.cards-grid/config {kws.config/page 5
                                                         kws.config/page-size 2}}))))

(deftest test-set-config-profile
  (let [config {kws.config/exclude-tags ["A"]
                kws.config/include-tags ["B"]
                kws.config/page 1
                kws.config/page-size 2
                kws.config/tags-filter-query "(FOO)"}
        profile {kws.profile/name "FOO"
                 kws.profile/config config}]

    (is (= {kws.cards-grid/config config} (sut/set-config-profile {} profile)))))

(deftest test-fetch-cards-params

  (let [config {kws.config/page 1
                kws.config/page-size 2
                kws.config/include-tags ["A"]
                kws.config/exclude-tags ["C"]
                kws.config/tags-filter-query "(tags CONTAINS 'foo')"}
        source-params {kws.cards-grid/config config}
        expected-params {kws.fetch-cards/config config}]

    (testing "Base"
      (is (= expected-params (sut/fetch-cards-params source-params))))))

(deftest test-state-not-initialized?

  (testing "nil -> false"
    (is (false? (sut/state-initialized? {}))))

  (testing "::ready -> true"
    (is (true? (sut/state-initialized? {kws.cards-grid/status kws.cards-grid/status-ready})))))

(deftest test-refetch-from-props!
  (with-redefs [sut/refetch! #(do [%1 %2])]
    (is (= [1 2] (sut/refetch-from-props! {:state 1 kws.cards-grid/fetch-cards! 2})))))

(defn gen-props [] {:state (atom {})})

(deftest test-set-page-from-props
  (testing "Sets page"
    (with-redefs [sut/refetch! #(do ::res)]
      (let [props (gen-props)]
        (is (= ::res (sut/set-page-from-props! props 10)))
        (is (= 10 (-> props :state deref kws.cards-grid/config kws.config/page)))))))

(deftest test-set-page-size-from-props
  (testing "Sets page size"
    (with-redefs [sut/refetch! #(do ::res)]
      (let [props (gen-props)]
        (is (= ::res (sut/set-page-size-from-props! props 10)))
        (is (= 10 (-> props :state deref kws.cards-grid/config kws.config/page-size)))))))

(deftest test-set-include-tags-from-props
  (testing "Sets include tags"
    (with-redefs [sut/refetch! #(do ::res)]
      (let [props (gen-props)]
        (is (= ::res (sut/set-include-tags-from-props! props ["FOO" ""])))
        (is (= ["FOO"] (-> props :state deref kws.cards-grid/config kws.config/include-tags)))))))

(deftest test-set-exclude-tags-from-props
  (testing "Sets exclude tags"
    (with-redefs [sut/refetch! #(do ::res)]
      (let [props (gen-props)]
        (is (= ::res (sut/set-exclude-tags-from-props! props ["FOO" ""])))
        (is (= ["FOO"] (-> props :state deref kws.cards-grid/config kws.config/exclude-tags)))))))

(deftest test-set-tags-filter-query-from-props
  (testing "Sets query"
    (with-redefs [sut/refetch! #(do ::res)]
      (let [props (gen-props)]
        (is (= ::res (sut/set-tags-filter-query-from-props! props "(FOO)")))
        (is (= "(FOO)" (-> props :state deref kws.cards-grid/config kws.config/tags-filter-query)))))))

(deftest has-previous-page?
  (is (true? (sut/has-previous-page? {kws.cards-grid/config {kws.config/page 2}})))
  (is (false? (sut/has-previous-page? {kws.cards-grid/config {kws.config/page 1}}))))
