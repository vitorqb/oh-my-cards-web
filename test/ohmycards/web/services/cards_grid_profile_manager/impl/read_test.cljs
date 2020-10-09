(ns ohmycards.web.services.cards-grid-profile-manager.impl.read-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.cards-grid-profile-manager.impl.read :as sut]))

(def http-response
  {kws.http/success? true
   kws.http/body {:name "Foo"
                  :config {:page 1
                           :excludeTags []
                           :includeTags ["A"]
                           :pageSize 2
                           :query "(FOO)"}}})

(deftest test-action
  (let [profile-name "Foo"
        action (sut/->Action profile-name)]

    (testing "Calls correct url"
      (is (= "/v1/cards-grid-profile/Foo" (protocols.http/url action))))

    (testing "Parses success response"
      (is (= {kws/success? true
              kws/fetched-profile {kws.profile/name "Foo"
                                   kws.profile/config {kws.config/page 1
                                                       kws.config/exclude-tags []
                                                       kws.config/include-tags ["A"]
                                                       kws.config/page-size 2
                                                       kws.config/tags-filter-query "(FOO)"}}}
             (protocols.http/parse-success-response action http-response))))))

(deftest test-profile-exists?
  (async
   done
   (a/go

     ;; Case 1: found
     (let [opts {:run-http-action-fn #(a/go {kws.http/success? true kws.http/status 200})}]
       (is (true? (a/<! (sut/profile-exists? opts "Foo")))))

     ;; Case 2: not found
     (let [opts {:run-http-action-fn #(a/go {kws.http/success? false kws.http/status 404})}]
       (is (nil? (a/<! (sut/profile-exists? opts "FOo")))))

     (done))))
