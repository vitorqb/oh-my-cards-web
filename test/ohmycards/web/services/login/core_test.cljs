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

(deftest test-set-user!
  (testing "Set's correct lens on the state"
    (binding [sut/*state* (atom {})]
      (let [user {kws.user/email "FOO"}]
        (sut/set-user! user)
        (is (= {lenses.login/current-user user} @sut/*state*))))))

(deftest test-is-logged-in

  (testing "True"
    (binding [sut/*state* (atom {})]
      (sut/set-user! {:email "foo@bar.com"})
      (is (true? (sut/is-logged-in?)))))

  (testing "False"
    (binding [sut/*state* (atom {})]
      (is (false? (sut/is-logged-in?))))))
