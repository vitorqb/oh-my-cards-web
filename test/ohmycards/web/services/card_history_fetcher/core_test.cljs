(ns ohmycards.web.services.card-history-fetcher.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.common.cards.history.core :as kws.cards.history]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.card-history-fetcher.core :as kws]
            [ohmycards.web.services.card-history-fetcher.core :as sut]))

(deftest test-parse-response

  (testing "Returns error if http call fails"
    (is (= {kws/success? false
            kws/error-message "ERRORMSG"}
           (sut/parse-response {kws.http/body "ERRORMSG"
                                kws.http/success? false}))))

  (testing "Returns success if http call succeeds"
    (is (= {kws/success? true
            kws/history {kws.cards.history/events []}}
           (sut/parse-response {kws.http/success? true
                                kws.http/body {:history []}})))))
