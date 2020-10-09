(ns ohmycards.web.services.cards-grid-profile-manager.impl.create-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.cards-grid-profile-manager.impl.create :as sut]))

(deftest test-action
  (let [profile {kws.profile/name "Foo"
                 kws.profile/config {kws.config/page 1
                                     kws.config/exclude-tags []
                                     kws.config/include-tags ["A"]
                                     kws.config/page-size 2}}
        action (sut/->Action profile)]

    (testing "Calls correct url"
      (is (= "/v1/cards-grid-profile"
             (protocols.http/url action))))

    (testing "Correct http method"
      (is (= :POST
             (protocols.http/method action))))

    (testing "Correctly parses json params"
      (is (= {:name "Foo"
              :config {:page 1
                       :excludeTags []
                       :includeTags ["A"]
                       :pageSize 2
                       :query nil}}
             (protocols.http/json-params action))))

    (testing "Correctly parses the error responses"
      (is (= {kws/success? false}
             (protocols.http/parse-error-response action {}))))

    (testing "Correctly parses the success responses"
      (is (= {kws/success? true}
             (protocols.http/parse-success-response action {}))))))
