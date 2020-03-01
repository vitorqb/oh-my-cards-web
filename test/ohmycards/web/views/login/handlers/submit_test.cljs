(ns ohmycards.web.views.login.handlers.submit-test
  (:require [ohmycards.web.views.login.handlers.submit :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.services.login.core :as services.login]
            [ohmycards.web.kws.services.login.core :as services.login.kws]))

(deftest test-before-submit

  (testing "Assocs loading to true"
    (is (true? (-> {} sut/before-submit :loading?))))

  (testing "Cleans error message"
    (is (nil? (-> {:error-message "FOO"} sut/before-submit :error-message)))))

(deftest test-after-submit

  (testing "Assocs loading to false"
    (is (false? (:loading? (sut/after-submit {} {::kws.http/success? false}))))
    (is (false? (:loading? (sut/after-submit {} {::kws.http/success? true})))))

  (testing "Assocs error message if any"
    (let [error-message "FOO"
          new-state (sut/after-submit {} {::services.login.kws/error-message error-message})]
      (is (= error-message (:error-message new-state)))))

  (testing "Assocs token if any"
    (let [token "FOO"
          new-state (sut/after-submit {} {::services.login.kws/token token})]
      (is (= token (:token new-state)))))

  (testing "Assocs :onetime-password-sent? if action is :send-onetime-password."
    (let [new-state (sut/after-submit
                     {}
                     {::services.login.kws/action
                      ::services.login.kws/send-onetime-password-action})]
      (is (true? (:onetime-password-sent? new-state))))))
