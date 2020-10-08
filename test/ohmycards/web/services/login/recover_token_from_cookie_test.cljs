(ns ohmycards.web.services.login.recover-token-from-cookie-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.login.recover-token-from-cookie :as sut]))

(deftest test-action
  
  (let [action (sut/->Action)]

    (testing "Nil if not success"
      (is (nil? (protocols.http/parse-error-response action {}))))

    (testing "Assocs login"
      (let [response {::kws.http/body {:value "foo"} ::kws.http/success? true}]
        (is (= {:value "foo"}
               (protocols.http/parse-success-response action response)))))))
