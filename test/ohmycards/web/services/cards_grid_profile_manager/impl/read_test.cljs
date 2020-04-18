(ns ohmycards.web.services.cards-grid-profile-manager.impl.read-test
  (:require [ohmycards.web.services.cards-grid-profile-manager.impl.read :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [cljs.core.async :as a]))

(def http-response
  {kws.http/success? true
   kws.http/body {:name "Foo"
                  :config {:page 1
                           :excludeTags []
                           :includeTags ["A"]
                           :pageSize 2
                           :query "(FOO)"}}})

(deftest test-parse-result

  (testing "Success"
    (let [profile {kws.profile/name "Foo"
                   kws.profile/config {kws.config/page 1
                                       kws.config/exclude-tags []
                                       kws.config/include-tags ["A"]
                                       kws.config/page-size 2
                                       kws.config/tags-filter-query "(FOO)"}}]
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
     kws.http/url "/v1/cards-grid-profile/FOO"}
    (sut/run-http-call! {:http-fn hash-map} "FOO"))))

(deftest test-main!
  (async
   done
   (a/go
     (with-redefs [sut/run-http-call! #(a/go http-response)]
       (let [exp-profile {kws.profile/name "Foo"
                          kws.profile/config {kws.config/page 1
                                              kws.config/page-size 2
                                              kws.config/include-tags ["A"]
                                              kws.config/exclude-tags []
                                              kws.config/tags-filter-query "(FOO)"}}
             exp-result {kws/success? true
                         kws/fetched-profile exp-profile}
             result (a/<! (sut/main! {} "Foo"))]
         (is (= exp-result result))
         (done))))))

(deftest test-profile-exists?
  (async
   done
   (a/go

     ;; Case 1: found
     (with-redefs [sut/run-http-call! #(a/go {kws.http/success? true kws.http/status 200})]
       (is (true? (a/<! (sut/profile-exists? {} "Foo")))))

     ;; Case 2: not found
     (with-redefs [sut/run-http-call! #(a/go {kws.http/success? false kws.http/status 404})]
       (is (nil? (a/<! (sut/profile-exists? {} "FOo")))))

     (done))))
