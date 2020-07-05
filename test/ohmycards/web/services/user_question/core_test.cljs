(ns ohmycards.web.services.user-question.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.services.user-question.core :as sut]))

(deftest test-confirm-card-delete
  (let [card {kws.card/title "FOO"}]
    (with-redefs [sut/yes-no #(do [::yes-no %1])]
      (is (= (sut/confirm-card-delete card)
             [::yes-no "Are you sure you want to delete FOO?"])))))
