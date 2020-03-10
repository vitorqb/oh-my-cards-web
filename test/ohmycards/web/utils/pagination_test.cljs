(ns ohmycards.web.utils.pagination-test
  (:require [ohmycards.web.utils.pagination :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-last-page
  (is (= 1 (sut/last-page 1 1)))
  (is (= 1 (sut/last-page 10 1)))
  (is (= 2 (sut/last-page 10 19)))
  (is (= 2 (sut/last-page 10 20)))
  (is (= 3 (sut/last-page 10 21))))
