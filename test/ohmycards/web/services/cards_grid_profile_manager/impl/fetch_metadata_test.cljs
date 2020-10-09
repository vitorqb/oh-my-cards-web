(ns ohmycards.web.services.cards-grid-profile-manager.impl.fetch-metadata-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.metadata.core :as kws.cards-grid.metadata]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.cards-grid-profile-manager.impl.fetch-metadata
             :as
             sut]))

(deftest test-action
  (let [action (sut/->Action)]
    (testing "Parses response properly"
      (let [response {kws.http/body {:names ["FOO"]}}]
        (is (= {kws.cards-grid.metadata/profile-names ["FOO"]}
               (protocols.http/parse-success-response action response)))))))

