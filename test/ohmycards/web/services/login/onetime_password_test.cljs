(ns ohmycards.web.services.login.onetime-password-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.login.core :as kws]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.login.core :as services.login]
            [ohmycards.web.services.login.onetime-password :as sut]))

(deftest test-action

  (let [opts {kws/email "email@email.email"}
        action (sut/->Action opts)]

    (testing "Parsing response -> Empty response -> no error-message"
      (is (= {kws/action kws/send-onetime-password-action}
             (protocols.http/parse-success-response action {}))))

    (testing "Returns generic error msg if fails"
      (is (= {kws/error-message "Something went wrong when sending the one time password."
              kws/action kws/send-onetime-password-action}
             (protocols.http/parse-error-response action {}))))))
