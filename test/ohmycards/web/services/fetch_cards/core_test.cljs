(ns ohmycards.web.services.fetch-cards.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.fetch-cards.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.fetch-cards.core :as sut]))

(deftest test-action

  (let [opts {kws/config {kws.config/page 2
                          kws.config/page-size 20
                          kws.config/include-tags ["A" "B"]
                          kws.config/exclude-tags ["C" "D"]
                          kws.config/tags-filter-query "(tags NOT CONTAINS 'bar')"}
              kws/search-term "SEARCH_TERM"}
        action (sut/->Action opts)]

    (testing "Parses success response"
      (let [response {kws.http/body {:page 2
                                     :pageSize 1
                                     :items [{:id 1
                                              :title "Foo"
                                              :body "Bar"
                                              :tags ["A"]
                                              :ref 1}]
                                     :countOfItems 100}}]
        (is (= {kws/cards [{kws.card/id 1
                            kws.card/title "Foo"
                            kws.card/body "Bar"
                            kws.card/tags ["A"]
                            kws.card/created-at nil
                            kws.card/updated-at nil
                            kws.card/ref 1}]
                kws/page 2
                kws/page-size 1
                kws/count-of-cards 100}
               (protocols.http/parse-success-response action response)))))

    (testing "Parses error response"
      (let [response {kws.http/body "Error"}]
        (is (= {kws/error-message "Error"}
               (protocols.http/parse-error-response action response)))))))

(deftest test-fetch-query-params
  (let [opts  {kws/config
                {kws.config/page              2
                 kws.config/page-size         20
                 kws.config/include-tags      ["A" "B"]
                 kws.config/exclude-tags      ["C" "D"]
                 kws.config/tags-filter-query "(tags NOT CONTAINS 'bar')"}
                kws/search-term "SEARCH_TERM"}
        result {:page       2
                :pageSize   20
                :tags       "A,B"
                :tagsNot    "C,D"
                :query      "(tags NOT CONTAINS 'bar')"
                :searchTerm "SEARCH_TERM"}]

    (testing "Defaults page"
      (let [opts  (update opts kws/config assoc kws.config/page nil)
            result (assoc result :page sut/default-page)]
        (is (= result
               (-> opts sut/->Action protocols.http/query-params)))))

    (testing "Defaults page-size"
      (let [opts  (update opts kws/config assoc kws.config/page-size nil)
            result (assoc result :pageSize sut/default-page-size)]
        (is (= result
               (-> opts sut/->Action protocols.http/query-params)))))

    (testing "Ignore tags if not on params"
      (let [opts  (update opts kws/config dissoc kws.config/include-tags)
            result (dissoc result :tags)]
        (is (= result
               (-> opts sut/->Action protocols.http/query-params)))))

    (testing "Ignore tagsNot if not on params"
      (let [opts  (update opts kws/config dissoc kws.config/exclude-tags)
            result (dissoc result :tagsNot)]
        (is (= result
               (-> opts sut/->Action protocols.http/query-params)))))

    (testing "Ignore query if not on params"
      (let [opts  (update opts kws/config dissoc kws.config/tags-filter-query)
            result (dissoc result :query)]
        (is (= result
               (-> opts sut/->Action protocols.http/query-params)))))

    (testing "Ignore search term if not set"
      (let [opts  (dissoc opts kws/search-term)
            result (dissoc result :searchTerm)]
        (is (= result
               (-> opts sut/->Action protocols.http/query-params)))))

    (testing "Ignore search term if set to an empty string"
      (let [opts  (assoc opts kws/search-term "")
            result (dissoc result :searchTerm)]
        (is (= result
               (-> opts sut/->Action protocols.http/query-params)))))))
