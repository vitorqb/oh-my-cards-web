(ns ohmycards.web.common.tags.core-test
  (:require [ohmycards.web.common.tags.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-sanitize
  (is (= ["foo" "bar"] (sut/sanitize ["foo" "" "bar"]))))
