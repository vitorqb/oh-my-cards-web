(ns ohmycards.web.services.cards-metadata-fetcher.core-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.cards-metadata-fetcher.core :as sut]))

(deftest test-action
  (let [action (sut/->Action)]
    (testing "Parses response"
      (is (= {kws.card-metadata/tags ["A" "B"]}
             (protocols.http/parse-success-response action {kws.http/body {:tags ["A" "B"]}}))))))
