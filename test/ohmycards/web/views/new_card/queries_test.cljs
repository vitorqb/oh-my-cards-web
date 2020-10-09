(ns ohmycards.web.views.new-card.queries-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.views.new-card.queries :as sut]))

(def f-card-input {kws.card/body (coercion.result/success "body" "body")
                   kws.card/tags (coercion.result/success ["a"] ["a"])
                   kws.card/title (coercion.result/success "title" "title")})

(deftest test-card-form-input

  (testing "Base"
    (let [state {kws/card-input f-card-input}]
      (is (= {kws.card/body "body"
              kws.card/tags ["a"]
              kws.card/title "title"}
             (sut/card-form-input state))))))


(deftest test-form-has-errors?

  (testing "Error on tags"
    (let [state {kws/card-input (assoc f-card-input
                                       kws.card/tags (coercion.result/failure "A" "A"))}]
      (is (true? (sut/form-has-errors? state)))))

  (testing "Error on title"
    (let [state {kws/card-input (assoc f-card-input
                                       kws.card/title (coercion.result/failure "A" "A"))}]
      (is (true? (sut/form-has-errors? state)))))

  (testing "No errors"
    (let [state {kws/card-input f-card-input}]
      (is (false? (sut/form-has-errors? state))))))
