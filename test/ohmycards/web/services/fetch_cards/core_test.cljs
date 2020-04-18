(ns ohmycards.web.services.fetch-cards.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.fetch-cards.core :as kws]
            [ohmycards.web.services.fetch-cards.core :as sut]))

(deftest test-fetch-query-params
  (let [props {kws/config
               {kws.config/page 2
                kws.config/page-size 20
                kws.config/include-tags ["A" "B"]
                kws.config/exclude-tags ["C" "D"]
                kws.config/tags-filter-query "(tags NOT CONTAINS 'bar')"}}
        result {:page 2
                :pageSize 20
                :tags "A,B"
                :tagsNot "C,D"
                :query "(tags NOT CONTAINS 'bar')"}]

    (testing "Defaults page"
      (let [props  (update props kws/config dissoc kws.config/page)
            result (assoc result :page sut/default-page)]
        (is (= result (sut/fetch-query-params props)))))

    (testing "Defaults page-size"
      (let [props  (update props kws/config dissoc kws.config/page-size)
            result (assoc result :pageSize sut/default-page-size)]
        (is (= result (sut/fetch-query-params props)))))

    (testing "Ignore tags if not on params"
      (let [props  (update props kws/config dissoc kws.config/include-tags)
            result (dissoc result :tags)]
        (is (= result (sut/fetch-query-params props)))))

    (testing "Ignore tagsNot if not on params"
      (let [props  (update props kws/config dissoc kws.config/exclude-tags)
            result (dissoc result :tagsNot)]
        (is (= result (sut/fetch-query-params props)))))

    (testing "Ignore query if not on params"
      (let [props  (update props kws/config dissoc kws.config/tags-filter-query)
            result (dissoc result :query)]
        (is (= result (sut/fetch-query-params props)))))))

(deftest test-parse-fetch-response

  (testing "On failure..."
    (let [http-resp {kws.http/success? false kws.http/body "Error"}
          result    (sut/parse-fetch-response http-resp)]

      (testing "Returns error message"
        (is (= "Error" (kws/error-message result))))))

  (testing "On success..."
    (let [http-resp {kws.http/success? true
                     kws.http/body     {:page 2
                                        :pageSize 1
                                        :items [{:id 1 :title "Foo" :body "Bar" :tags ["A"]}]
                                        :countOfItems 100}}
          result    (sut/parse-fetch-response http-resp)]

      (testing "Returns cards"
        (is (= [{kws.card/id 1 kws.card/title "Foo" kws.card/body "Bar" kws.card/tags ["A"]}]
               (kws/cards result))))

      (testing "Returns page"
        (is (= 2 (kws/page result))))

      (testing "Returns page size"
        (is (= 1 (kws/page-size result))))

      (testing "Returns total cards count"
        (is (= 100 (kws/count-of-cards result)))))))

(deftest test-fetch!

  (testing "Calls http-fn with correct args"
    (let [config {kws.config/page 11 kws.config/page-size 100}]
      (is (= {kws.http/method :GET
              kws.http/url "/v1/cards"
              kws.http/query-params {:page 11 :pageSize 100}}
             (sut/fetch! {:http-fn hash-map kws/config config})))))

  (testing "Defaults page"
    (is (= sut/default-page
           (-> (sut/fetch! {:http-fn hash-map kws/page-size 100})
               kws.http/query-params :page))))

  (testing "Defaults page"
    (is (= sut/default-page-size
           (-> (sut/fetch! {:http-fn hash-map kws/page 11})
               kws.http/query-params :pageSize)))))
