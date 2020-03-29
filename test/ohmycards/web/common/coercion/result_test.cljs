(ns ohmycards.web.common.coercion.result-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.result :as sut]
            [ohmycards.web.kws.common.coercion.result :as kws]))

(deftest test-raw-value->success
  (is (= {kws/success? true kws/raw-value "foo" kws/value "foo"}
         (sut/raw-value->success "foo"))))

(deftest test-success
  (is (= {kws/success? true kws/raw-value "foo" kws/value "bar"}
         (sut/success "foo" "bar"))))

(deftest test-failure
  (is (= {kws/success? false kws/raw-value "foo" kws/error-message "bar"}
         (sut/failure "foo" "bar"))))

(deftest test-->failure
  (is (= {kws/success? false kws/raw-value "foo" kws/error-message "bar"}
         (sut/->failure (sut/success "foo" "foo") "bar"))))
