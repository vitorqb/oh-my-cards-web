(ns ohmycards.web.views.login.core-test
  (:require [ohmycards.web.views.login.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-one-time-password-row

  (testing "Don't display if onetimepassword not yet sent"
    (is (nil? (sut/one-time-password-row {:onetime-password-sent? false})))))
