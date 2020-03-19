(ns ohmycards.web.services.login.utils-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.user :as kws.user]
            [ohmycards.web.services.login.utils :as sut]))

(deftest test-user-logged-in?
  (is (false? (sut/user-logged-in? {})))
  (is (false? (sut/user-logged-in? {kws.user/email "FOO"})))
  (is (false? (sut/user-logged-in? {kws.user/token "FOO"})))
  (is (true? (sut/user-logged-in? {kws.user/token "FOO" kws.user/email "BAR"}))))
