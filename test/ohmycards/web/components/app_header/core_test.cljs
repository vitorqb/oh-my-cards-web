(ns ohmycards.web.components.app-header.core-test
  (:require [ohmycards.web.components.app-header.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-main

  (testing "Renders a span with title"
    (is
     (some
      #(= [:span.app-header__left "OhMyCards!"] %)
      (tree-seq vector? identity (sut/main {})))))

  (testing "Renders a span with the user email"
    (is
     (some
      #(= [:span.app-header__right "email"] %)
      (tree-seq vector? identity (sut/main {::sut/email "email"}))))))
