(ns ohmycards.web.common.utils-test
  (:require [ohmycards.web.common.utils :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-to-path
  (is (= [:k] (sut/to-path :k)))
  (is (= [:k] (sut/to-path [:k])))
  (is (= ["FOO"] (sut/to-path "FOO")))
  (is (nil? (sut/to-path {}))))
