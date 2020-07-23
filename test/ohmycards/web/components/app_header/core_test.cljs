(ns ohmycards.web.components.app-header.core-test
  (:require [ohmycards.web.components.app-header.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-main

  (testing "Renders a span with title"
    (is (= [:span.logo "OhMyCards!"] (-> {} sut/main second :left))))

  (testing "Renders a span with the user email"
    (is (= [:span.u-italic "email"] (-> {::sut/email "email"} sut/main second :right))))

  (testing "Passes extra class"
    (is (= "app-header" (-> {} sut/main second :extra-class)))))
