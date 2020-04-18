(ns ohmycards.web.services.fetch-cards.core-test
  (:require [ohmycards.web.services.fetch-cards.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.fetch-cards.core :as kws]
            [ohmycards.web.kws.card :as kws.card]))

(deftest test-fetch-query-params
  (let [props {kws/page 2
               kws/page-size 20
               kws/include-tags ["A" "B"]
               kws/exclude-tags ["C" "D"]
               kws/tags-filter-query "(tags NOT CONTAINS 'bar')"}
        result {:page 2
                :pageSize 20
                :tags "A,B"
                :tagsNot "C,D"
                :query "(tags NOT CONTAINS 'bar')"}]

    (testing "Defaults page"
      (is (= (assoc result :page sut/default-page)
             (sut/fetch-query-params (dissoc props kws/page)))))

    (testing "Defaults page-size"
      (is (= (assoc result :pageSize sut/default-page-size)
             (sut/fetch-query-params (dissoc props kws/page-size)))))

    (testing "Ignore tags if not on params"
      (is (= (dissoc result :tags)
             (sut/fetch-query-params (dissoc props kws/include-tags)))))

    (testing "Ignore tagsNot if not on params"
      (is (= (dissoc result :tagsNot)
             (sut/fetch-query-params (dissoc props kws/exclude-tags)))))

    (testing "Ignore query if not on params"
      (is (= (dissoc result :query)
             (sut/fetch-query-params (dissoc props kws/tags-filter-query)))))))

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
    (is (= {kws.http/method :GET
            kws.http/url "/v1/cards"
            kws.http/query-params {:page 11 :pageSize 100}}
           (sut/fetch! {:http-fn hash-map kws/page 11 kws/page-size 100}))))

  (testing "Defaults page"
    (is (= sut/default-page
           (-> (sut/fetch! {:http-fn hash-map kws/page-size 100})
               kws.http/query-params :page))))

  (testing "Defaults page"
    (is (= sut/default-page-size
           (-> (sut/fetch! {:http-fn hash-map kws/page 11})
               kws.http/query-params :pageSize)))))
