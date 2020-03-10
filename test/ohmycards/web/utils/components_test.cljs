(ns ohmycards.web.utils.components-test
  (:require [ohmycards.web.utils.components :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-with-seq-keys

  (is (= {:key 0} (-> [{}] sut/with-seq-keys first meta)))
  (is (= {:key 1} (-> [{} {}] sut/with-seq-keys second meta))))
