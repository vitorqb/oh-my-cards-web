(ns ohmycards.web.services.cards-grid-profile-manager.impl.save-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.services.cards-grid-profile-manager.impl.save :as sut]))

(deftest test-parse-result

  (testing "Failure"
    (is (= {kws/success? false} (sut/parse-result {kws.http/success? false}))))

  (testing "Success"
    (is (= {kws/success? true} (sut/parse-result {kws.http/success? true})))))

(deftest test-run-http-call!
  (is
   (=
    {kws.http/method :POST
     kws.http/url "/v1/cards-grid-profile"
     kws.http/json-params {:name "Foo"
                           :config {:page 1 :excludeTags [] :includeTags ["A"] :pageSize 2}}}
    (sut/run-http-call! {:http-fn hash-map}
                        {kws.profile/name "Foo"
                         kws.profile/config {kws.config/page 1
                                             kws.config/exclude-tags []
                                             kws.config/include-tags ["A"]
                                             kws.config/page-size 2}}))))

(deftest test-main!
  (async
   done
   (a/go
     (with-redefs [sut/run-http-call! #(a/go {kws.http/success? true})]
       (is (= {kws/success? true}
              (a/<! (sut/main! {} {}))))
       (done)))))
