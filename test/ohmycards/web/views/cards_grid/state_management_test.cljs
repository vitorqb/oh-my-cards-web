(ns ohmycards.web.views.cards-grid.state-management-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.services.fetch-cards.core :as kws.fetch-cards]
            [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [ohmycards.web.views.cards-grid.state-management :as sut]))

(deftest test-reduce-before-fetch-cards
  (is (= {::foo 1 kws.cards-grid/status kws.cards-grid/status-loading}
         (sut/reduce-before-fetch-cards {::foo 1}))))

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
        source-params {kws.cards-grid/config config
                       kws.cards-grid/search-term "FOO"}
        expected-params {kws.fetch-cards/config config
                         kws.fetch-cards/search-term "FOO"}]

    (testing "Base"
      (is (= expected-params (sut/fetch-cards-params source-params))))))

(deftest test-refetch-from-props!
  (with-redefs [sut/refetch! #(do [%1 %2])]
    (is (= [1 2] (sut/refetch-from-props! {:state 1 kws.cards-grid/fetch-cards! 2})))))

(deftest test-toggle-filter!

  (with-redefs [sut/refetch-from-props! #(do [::result %1])]

    (testing "From empty"
      (let [state (atom {})]
        (sut/toggle-filter! {:state state})
        (is (true? (kws.cards-grid/filter-enabled? @state)))))

    (testing "From true"
      (let [state (atom {kws.cards-grid/filter-enabled? true})]
        (sut/toggle-filter! {:state state})
        (is (false? (kws.cards-grid/filter-enabled? @state)))))

    (testing "From false"
      (let [state (atom {kws.cards-grid/filter-enabled? false})]
        (sut/toggle-filter! {:state state})
        (is (true? (kws.cards-grid/filter-enabled? @state)))))

    (testing "When setting filter off..."
      
      (testing "cleans the committed search term"
        (let [state (atom {kws.cards-grid/filter-enabled? true
                           kws.cards-grid/search-term     "FOO"})]
          (sut/toggle-filter! {:state state})
          (is (= "" (kws.cards-grid/search-term @state)))))

      (testing "calls refetch-from-props!"
        (let [state (atom {kws.cards-grid/filter-enabled? true})
              props {:state state}]
          (is (= [::result props] (sut/toggle-filter! props))))))))

(deftest test-commit-search!

  (with-redefs [sut/refetch-from-props! #(do [::result %1])]

    (testing "When called..."
      
      (let [state  (atom {kws.cards-grid/filter-input-search-term "FOO"})
            props  {:state state}
            result (sut/commit-search! props)]

        (testing "Set's the committed search-term from the input search term"
          (is (= "FOO" (kws.cards-grid/search-term @state))))

        (testing "Set's page back to 1"
          (is (= 1 (-> @state kws.cards-grid/config kws.config/page))))

        (testing "Refetches the grid"
          (is (= result [::result props])))))

    (testing "Don't do anything if search term has not changed"
      
      (let [state  {kws.cards-grid/filter-input-search-term "FOO"
                    kws.cards-grid/search-term "FOO"}
            state' (atom state)
            props  {:state state'}
            result (sut/commit-search! props)]

        (is (= state @state'))
        (is (nil? result))))))

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

(deftest test-loading?

  (testing "True if nil"
    (is (true? (sut/loading? {:state (atom {})}))))

  (testing "True if loading status"
    (is
     (true?
      (sut/loading? {:state (atom {kws.cards-grid/status kws.cards-grid/status-loading})}))))

  (testing "False if status set"
    (is
     (false?
      (sut/loading? {:state (atom {kws.cards-grid/status kws.cards-grid/status-ready})})))))
