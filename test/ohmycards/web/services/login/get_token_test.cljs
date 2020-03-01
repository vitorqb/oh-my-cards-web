(ns ohmycards.web.services.login.get-token-test
  (:require [ohmycards.web.services.login.get-token :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.services.login.core :as kws]
            [ohmycards.web.kws.http :as kws.http]))

(deftest test-parse-response

  (testing "Something went wrong error message if status is 5xx"
    (is (= {::kws/action ::kws/get-token-action
            ::kws/error-message "Something went wrong."}
           (sut/parse-response {::kws.http/status 501}))))

  (testing "Invalid credentials if 400"
    (is (= {::kws/action ::kws/get-token-action
            ::kws/error-message "Invalid credentials."}
           (sut/parse-response {::kws.http/status 400}))))

  (testing "Valid response."
    (is (= {::kws/action ::kws/get-token-action
            ::kws/token {:value "FOO"}}
           (sut/parse-response {::kws.http/success? true
                                ::kws.http/body {:value "FOO"}})))))
