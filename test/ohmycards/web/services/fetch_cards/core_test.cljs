(ns ohmycards.web.services.fetch-cards.core-test
  (:require [ohmycards.web.services.fetch-cards.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.fetch-cards.core :as kws]
            [ohmycards.web.kws.card :as kws.card]))

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
                                        :items [{:id 1 :title "Foo" :body "Bar"}]
                                        :countOfItems 100}}
          result    (sut/parse-fetch-response http-resp)]

      (testing "Returns cards"
        (is (= [{kws.card/id 1 kws.card/title "Foo" kws.card/body "Bar"}]
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
