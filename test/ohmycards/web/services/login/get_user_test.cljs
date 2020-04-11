(ns ohmycards.web.services.login.get-user-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.user :as kws.user]
            [ohmycards.web.services.login.get-user :as sut]))

(deftest test-parse-get-user-response
  (testing "Base"
    (let [response {kws.http/body {:email "a@b"}}]
      (is (= {kws.user/email "a@b" kws.user/token "token"}
             (sut/parse-get-user-response response "token"))))))

(deftest test-run-get-user-http-call
  (testing "Calls http-fn with correct values"
    (is (= {kws.http/url "/v1/auth/user"
            kws.http/method :get
            kws.http/token "token"}
           (sut/run-get-user-http-call! {:http-fn hash-map} "token")))))

