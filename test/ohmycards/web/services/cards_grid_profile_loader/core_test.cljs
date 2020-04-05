(ns ohmycards.web.services.cards-grid-profile-loader.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.services.cards-grid-profile-loader.core :as sut]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.services.cards-grid-profile-loader.core :as kws]))

(deftest test-parse-result

  (testing "Success"
    (let [profile {kws.profile/name "Foo"
                   kws.profile/config {kws.config/page 1
                                       kws.config/exclude-tags []
                                       kws.config/include-tags ["A"]
                                       kws.config/page-size 2}}
          http-response {kws.http/success? true
                         kws.http/body {:name "Foo" :config {:page 1
                                                             :excludeTags []
                                                             :includeTags ["A"]
                                                             :pageSize 2}}}]
      (is
       (=
        {kws/success? true kws/fetched-profile profile}
        (sut/parse-result http-response)))))

  (testing "Failure"
    (let [http-response {kws.http/success? false}]
      (is (= {kws/success? false} (sut/parse-result http-response))))))

(deftest test-run-http-call!
  (is
   (=
    {kws.http/method :GET
     kws.http/url "/v1/cards-grid-profile"
     kws.http/query-params {:profile-name "FOO"}}
    (sut/run-http-call! {:http-fn hash-map} "FOO"))))
