(ns ohmycards.web.components.header.core-test
  (:require [ohmycards.web.components.header.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-main

  (testing "Renders a span with title"
    (is
     (some
      #(= [:span.title "OhMyCards!"] %)
      (tree-seq vector? identity (sut/main {}))))))
