(ns ohmycards.web.common.coercion.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.common.coercion.core :as sut]
            [ohmycards.web.common.coercion.result :as result]))

(deftest test-main

  (testing "With identity as coercer..."
    (testing "Returns a success with the raw value"
      (is (= (result/raw-value->success "foo")
             (sut/main "foo" identity)))))

  (testing "String->Integer coercion..."
    (let [coercer #(-> % coercers/integer coercers/positive)]

      (testing "Valid"
        (is (= (result/success "10" 10)
               (sut/main "10" coercer)))
        (is (= (result/success "999" 999)
               (sut/main "999" coercer))))

      (testing "Invalid - negative"
        (is (= (result/failure "-10" coercers/not-positive)
               (sut/main "-10" coercer)))
        (is (= (result/failure "0" coercers/not-positive)
               (sut/main "0" coercer))))

      (testing "Not an integer"
        (is (= (result/failure "foo" coercers/not-an-integer)
               (sut/main "foo" coercer)))
        (is (= (result/failure "1.1" coercers/not-an-integer)
               (sut/main "1.1" coercer)))
        (is (= (result/failure "1." coercers/not-an-integer)
               (sut/main "1." coercer)))
        (is (= (result/failure ".1" coercers/not-an-integer)
               (sut/main ".1" coercer)))
        (is (= (result/failure 2.21 coercers/not-an-integer)
               (sut/main 2.21 coercer)))))))

(deftest test-Or-coercer

  (let [good-result  (result/success "2" 2)
        good-coercer (reify sut/Coercer
                       (-coerce [_ r] good-result))
        bad-result   (result/failure "val" "err")
        bad-coercer  (reify sut/Coercer
                       (-coerce [_ r] bad-result))]

    (testing "With a single coercer"

      (testing "returns the bad result if it receives a bad result"
        (let [or-coercer (sut/->Or [good-coercer])]
          (is (= bad-result (sut/-coerce or-coercer bad-result)))))
      
      (testing "returns the good coercer if it receives a good result"
        (let [or-coercer (sut/->Or [good-coercer])]
          (is (= good-result (sut/-coerce or-coercer good-result)))))

      (testing "returns a bad coercer if the inner coercer fails"
        (let [or-coercer (sut/->Or [bad-coercer])]
          (is (= bad-result (sut/-coerce or-coercer good-result))))))

    (testing "With two coercers"

      (testing "returns the bad result if it receives a bad result"
        (let [or-coercer (sut/->Or [good-coercer good-coercer])]
          (is (= bad-result (sut/-coerce or-coercer bad-result)))))
      
      (testing "if the first coercer returns a good result, keep it."
        (let [or-coercer (sut/->Or [good-coercer bad-coercer])]
          (is (= good-result (sut/-coerce or-coercer good-result)))))

      (testing "if the second coercer returns a good result, keep it."
        (let [or-coercer (sut/->Or [bad-coercer good-coercer])]
          (is (= good-result (sut/-coerce or-coercer good-result)))))

      (testing "if both coercers returns a bad result, returns the last bad result."
        (let [or-coercer (sut/->Or [bad-coercer bad-coercer])]
          (is (= bad-result (sut/-coerce or-coercer good-result))))))))

(deftest test-coerce-for-fns
  (is (= 1 (sut/-coerce #(do %) 1)))
  (is (= 1 (sut/-coerce (constantly 1) nil))))
