(ns ohmycards.web.services.cards-grid-profile-manager.core-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.metadata.core :as kws.cards-grid.metadata]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.services.cards-grid-profile-manager.core :as sut]
            [ohmycards.web.services.cards-grid-profile-manager.fetch-metadata
             :as
             fetch-metadata]))

(deftest test-parse-load-result

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
        (sut/parse-load-result http-response)))))

  (testing "Failure"
    (let [http-response {kws.http/success? false}]
      (is (= {kws/success? false} (sut/parse-load-result http-response))))))

(deftest test-parse-save-result

  (testing "Failure"
    (is (= {kws/success? false} (sut/parse-save-result {kws.http/success? false}))))

  (testing "Success"
    (is (= {kws/success? true} (sut/parse-save-result {kws.http/success? true})))))

(deftest test-run-load-http-call!
  (is
   (=
    {kws.http/method :GET
     kws.http/url "/v1/cards-grid-profile/FOO"}
    (sut/run-load-http-call! {:http-fn hash-map} "FOO"))))

(deftest test-run-save-http-call!
  (is
   (=
    {kws.http/method :POST
     kws.http/url "/v1/cards-grid-profile"
     kws.http/json-params {:name "Foo"
                           :config {:page 1 :excludeTags [] :includeTags ["A"] :pageSize 2}}}
    (sut/run-save-http-call! {:http-fn hash-map}
                             {kws.profile/name "Foo"
                              kws.profile/config {kws.config/page 1
                                                  kws.config/exclude-tags []
                                                  kws.config/include-tags ["A"]
                                                  kws.config/page-size 2}}))))

(deftest test-fetch-metadata!*__base
  (let [fetch-response {kws.http/body {:names ["FOO" "BAR"]}}
        parsed-response (fetch-metadata/parse-response fetch-response)
        do-fetch-metadata! #(a/go parsed-response)
        on-metadata-fetch #(assoc % ::foo 1)
        opts {kws/on-metadata-fetch on-metadata-fetch}
        response-chan (sut/fetch-metadata!* opts do-fetch-metadata!)]
    (async done
           (a/go
             (is (= {kws.cards-grid.metadata/profile-names ["FOO" "BAR"] ::foo 1}
                    (a/<! response-chan)))
             (done)))))
