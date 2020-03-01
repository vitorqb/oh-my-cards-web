(ns ohmycards.web.services.login.core-test
  (:require [ohmycards.web.services.login.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.login.core :as sut.kws]
            [ohmycards.web.services.login.onetime-password :as onetime-password]))

(deftest test-main

  (testing "Dispatches to send-onetime-password! if no onetime-password"
    (with-redefs [onetime-password/send! #(do [::foo %1 %2])]
      (let [args {::args 1} opts {::opts 1}]
        (is (= [::foo args opts] (sut/main args opts))))))

  (testing "Dispatches to get-token! if has onetime-password"
    (with-redefs [sut/get-token! #(do [::foo %1 %2])]
      (let [args {::sut.kws/onetime-password 1} opts {::opts 1}]
        (is (= [::foo args opts] (sut/main args opts)))))))
