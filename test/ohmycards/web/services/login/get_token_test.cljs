(ns ohmycards.web.services.login.get-token-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.login.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.login.get-token :as sut]))

(deftest test-parse-response

  (let [action (sut/->Action {})]

    (testing "Something went wrong error message if status is 5xx"
      (is (= {kws/action kws/get-token-action
              kws/error-message "Something went wrong."}
             (protocols.http/parse-error-response action {kws.http/status 501}))))

    (testing "Invalid credentials if 400"
      (is (= {kws/action kws/get-token-action
              kws/error-message "Invalid credentials."}
             (protocols.http/parse-error-response action {kws.http/status 400}))))

    (testing "Valid response."
      (is (= {kws/action kws/get-token-action
              kws/token {:value "FOO"}}
             (protocols.http/parse-success-response action {kws.http/body {:value "FOO"}}))))))
