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
