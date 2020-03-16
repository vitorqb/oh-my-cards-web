(ns ohmycards.web.services.http.utils-test
  (:require [ohmycards.web.services.http.utils :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-body->err-msg
  (is (= "FOO" (sut/body->err-msg nil "FOO")))
  (is (= "FOO" (sut/body->err-msg "" "FOO")))
  (is (= "BAR" (sut/body->err-msg "BAR" "FOO"))))
