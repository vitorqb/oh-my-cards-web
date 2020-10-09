(ns ohmycards.web.services.card-history-fetcher.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.common.cards.history.core :as kws.cards.history]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.card-history-fetcher.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.card-history-fetcher.core :as sut]))

(def action (sut/->Action "id"))

(deftest test-url
  (is (= "/v1/cards/id/history" (protocols.http/url action))))

(deftest test-parse-success-response
  (is (= {kws/success? true kws/history {kws.cards.history/events []}}
         (protocols.http/parse-success-response
          action
          {kws.http/success? true kws.http/body {:history []}}))))

(deftest test-parse-error-response
  (is (= {kws/success? false kws/error-message "ERRORMSG"}
         (protocols.http/parse-error-response
          action
          {kws.http/body "ERRORMSG" kws.http/success? false}))))
