(ns ohmycards.web.services.login.get-user-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.user :as kws.user]
            [ohmycards.web.services.login.get-user :as sut]))

(deftest test-parse-response
  (testing "Base"
    (let [response {kws.http/body {:email "a@b"}}]
      (is (= {kws.user/email "a@b" kws.user/token "token"}
             (sut/parse-response response "token"))))))

(deftest test-run-http-call!
  (testing "Calls http-fn with correct values"
    (is (= {kws.http/url "/v1/auth/user"
            kws.http/method :get
            kws.http/token "token"}
           (sut/run-http-call! {:http-fn hash-map} "token")))))

(deftest test-main!*
  (let [run-http-call! (fn [& _] (a/go {kws.http/body {:email "a@b"}}))
        response-chan (sut/main!* {} "token" run-http-call!)]
    (async done
           (a/go
             (is (= {kws.user/email "a@b" kws.user/token "token"}
                    (a/<! response-chan)))
             (done)))))
