(ns ohmycards.web.common.storage.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.storage.core :as sut]
            [ohmycards.web.kws.common.storage.core :as kws]))

(deftest test-storage

  (testing "Put and peek"
    (let [storage (sut/new-storage {kws/state (atom nil)})
          obj {:foo "bar"}
          key (sut/put storage obj)]
      (is (string? key))
      (is (= obj (sut/peek storage key)))
      (is (nil? (sut/peek storage obj))))))
