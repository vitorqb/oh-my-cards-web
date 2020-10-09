(ns ohmycards.web.services.cards-grid-profile-manager.impl.update-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.cards-grid-profile-manager.impl.helpers
             :as
             helpers]
            [ohmycards.web.services.cards-grid-profile-manager.impl.update :as sut]))

(deftest test-action
  (let [profile {kws.profile/name "FOO"}
        action (sut/->Action profile)]

    (testing "Calls correct url"
      (is (= "/v1/cards-grid-profile/FOO"
             (protocols.http/url action))))))
