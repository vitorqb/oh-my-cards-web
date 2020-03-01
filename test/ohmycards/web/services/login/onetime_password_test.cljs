(ns ohmycards.web.services.login.onetime-password-test
  (:require [ohmycards.web.services.login.onetime-password :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.login.core :as services.login.kws]
            [ohmycards.web.services.login.core :as services.login]))

(deftest test-parse-response

  (testing "Empty response -> no error-message"
    (is (= {::services.login.kws/action services.login.kws/send-onetime-password-action}
           (sut/parse-response {::kws.http/success? true}))))

  (testing "Returns generic error msg if fails"
    (is (= {::services.login.kws/error-message "Something went wrong when sending the one time password."
            ::services.login.kws/action services.login.kws/send-onetime-password-action}
           (sut/parse-response {::kws.http/success? false})))))
