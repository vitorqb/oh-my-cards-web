(ns ohmycards.web.components.inputs.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.kws.common.coercion.result :as kws.coercion.result]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.components.inputs.core :as sut]
            [ohmycards.web.kws.components.inputs.core :as kws]))

(deftest test-parse-props

  (let [make-props #(merge {kws/props {::foo ::bar}
                            kws/itype kws/t-simple
                            kws/cursor (atom nil)}
                           %)
        make-result #(sut/parse-props (make-props %))]

    (testing "Passes `props` down to the input implementation"
      (is (= ::bar (::foo (make-result {})))))

    (testing "Passes input itype to the multimethod"
      (is (= kws/t-simple (kws/itype (make-result {})))))

    (testing "Changes atom on `on-change`"
      (let [cursor (atom {})
            result (make-result {kws/cursor cursor})]
        ((:on-change result) ::foo)
        (is (= ::foo @cursor))))

    (testing "Changes atom on `on-change` if coercion is defined"
      (let [cursor (atom nil)
            coercer coercers/integer
            result (make-result {kws/cursor cursor kws/coercer coercer})]
        ((:on-change result) "1")
        (is (= "1" (kws.coercion.result/raw-value @cursor)))
        (is (= 1 (kws.coercion.result/value @cursor)))))

    (testing "Set's input :value from cursor atom"
      (let [value "foo"
            cursor (atom value)
            result (make-result {kws/cursor cursor})]
        (is (= "foo" (:value result)))))

    (testing "Set's input :value from cursor atom with coercion result"
      (let [value (coercion.result/raw-value->success "foo")
            coercer coercers/integer
            cursor (atom value)
            result (make-result {kws/cursor cursor kws/coercer coercer})]
        (is (= "foo" (:value result)))))

    (testing "Set's disabled if passed"
      (is (nil? (:disabled (make-result {}))))
      (is (true? (:disabled (make-result {kws/disabled? true}))))
      (is (false? (:disabled (make-result {kws/disabled? false})))))

    (testing "Set's auto-focus if passed"
      (is (nil? (:auto-focus (make-result {}))))
      (is (true? (:auto-focus (make-result {kws/auto-focus true}))))
      (is (false? (:auto-focus (make-result {kws/auto-focus false})))))))

(deftest test-main

  (with-redefs [sut/parse-props #(do {::parse-props 1 ::args %&})]

    (let [props {kws/itype kws/t-tags kws/cursor (atom nil)}
          component (sut/main props)]

      (testing "Renders component using `impl`"
        (is (= sut/impl (first component))))

      (testing "Passes props with `parse-prosp` `impl`"
        (is (= (sut/parse-props props) (second component)))))))
