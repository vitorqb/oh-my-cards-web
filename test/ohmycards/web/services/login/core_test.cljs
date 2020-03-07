(ns ohmycards.web.services.login.core-test
  (:require [ohmycards.web.services.login.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.login.core :as sut.kws]
            [ohmycards.web.services.login.onetime-password :as onetime-password]
            [ohmycards.web.services.login.get-token :as get-token]
            [ohmycards.web.kws.lenses.login :as lenses.login]
            [ohmycards.web.kws.user :as kws.user]))

(deftest test-main

  (testing "Dispatches to send-onetime-password! if no onetime-password"
    (with-redefs [onetime-password/send! #(do [::foo %1 %2])]
      (let [args {::args 1} opts {::opts 1}]
        (is (= [::foo args opts] (sut/main args opts))))))

  (testing "Dispatches to get-token! if has onetime-password"
    (with-redefs [get-token/send! #(do [::foo %1 %2])]
      (let [args {::sut.kws/onetime-password 1} opts {::opts 1}]
        (is (= [::foo args opts] (sut/main args opts)))))))


(deftest test-parse-token-recovery-response

  (let [state {:foo :bar}
        response {::kws.http/body {:value "foo"} ::kws.http/success? true}]

    (testing "Identity if not body"
      (let [response* (assoc response ::kws.http/body nil)]
        (is (= state (sut/parse-token-recovery-response state response*)))))

    (testing "Identity if not success"
      (let [response* (assoc response ::kws.http/success? false)]
        (is (= state (sut/parse-token-recovery-response state response*)))))

    (testing "Assocs login"
      (is (= (assoc-in state [lenses.login/current-user kws.user/token] {:value "foo"})
             (sut/parse-token-recovery-response state response))))))

(deftest test-should-try-to-recover-user?

  (let [state {lenses.login/current-user {kws.user/email "bar@foo.com"
                                          kws.user/token "FOO"}}]

    (testing "False if already has token and email"
      (is (false? (sut/should-try-to-recover-user? state))))

    (testing "False if has no token"
      (let [state* (assoc-in state [lenses.login/current-user kws.user/token] nil)]
        (is (nil? (sut/should-try-to-recover-user? state*)))))

    (testing "True if has token but no email"
      (let [state* (assoc-in state [lenses.login/current-user kws.user/email] nil)]
        (is (true? (sut/should-try-to-recover-user? state*)))))))

(deftest test-parse-get-user-response

  (let [state {lenses.login/current-user {kws.user/token "FOO"}}]

    (testing "Sets the email from response"
      (is (= (assoc-in state [lenses.login/current-user kws.user/email] "a@b.c")
             (sut/parse-get-user-response state {::kws.http/body {:email "a@b.c"}}))))))
