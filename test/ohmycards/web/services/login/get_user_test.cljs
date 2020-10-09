(ns ohmycards.web.services.login.get-user-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.user :as kws.user]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.login.get-user :as sut]))

(deftest test-action

  (let [action (sut/->Action "token")]

    (testing "Response parsing"
      (let [response {kws.http/body {:email "a@b"}}]
        (is (= {kws.user/email "a@b" kws.user/token "token"}
               (protocols.http/parse-success-response action response)))))))
