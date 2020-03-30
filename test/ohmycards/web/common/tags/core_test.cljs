(ns ohmycards.web.common.tags.core-test
  (:require [ohmycards.web.common.tags.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-sanitize
  (is (= ["foo" "bar"] (sut/sanitize ["foo" "" "bar"]))))

(deftest test-valid?

  (testing "False if is empty"
    (is (false? (sut/valid? "")))
    (is (false? (sut/valid? nil))))

  (testing "False if has a space"
    (is (false? (sut/valid? "a b"))))

  (testing "True"
    (is (true? (sut/valid? "DONE")))))
