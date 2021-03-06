(ns ohmycards.web.common.coercion.coercers-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.coercers :as sut]
            [ohmycards.web.common.coercion.result :as coercion.result]))

(deftest test-wrap-success

  (testing "Calls fn if result is success"
    (let [f       (fn [x] [::fn x])
          result  (coercion.result/raw-value->success "foo")
          wrapped (sut/wrap-success f)]
      (is (= [::fn result] (wrapped result)))))

  (testing "Returns result unchanged if result is failure."
    (let [f       (fn [_] (throw "SHOULD NOT HAVE REACHED IT"))
          result  (coercion.result/failure "" "")
          wrapped (sut/wrap-success f)]
      (is (= result (wrapped result))))))


(deftest test-integer

  (testing "Valid coercion"
    (is (= (coercion.result/success "1" 1)
           (sut/integer (coercion.result/raw-value->success "1"))))
    (is (= (coercion.result/success "999" 999)
           (sut/integer (coercion.result/raw-value->success "999"))))
    (is (= (coercion.result/success 1 1)
           (sut/integer (coercion.result/raw-value->success 1)))))

  (testing "Invalid coercion"
    (is (= (coercion.result/failure "" sut/not-an-integer)
           (sut/integer (coercion.result/raw-value->success ""))))
    (is (= (coercion.result/failure "foo" sut/not-an-integer)
           (sut/integer (coercion.result/raw-value->success "foo"))))
    (is (= (coercion.result/failure "2.2" sut/not-an-integer)
           (sut/integer (coercion.result/raw-value->success "2.2"))))))

(deftest test-positive

  (testing "Valid coercion"
    (is (= (coercion.result/success 1 1)
           (sut/positive (coercion.result/raw-value->success 1)))))

  (testing "Failed coercion"
    (is (= (coercion.result/failure -1 sut/not-positive)
           (sut/positive (coercion.result/raw-value->success -1))))
    (is (= (coercion.result/failure 0 sut/not-positive)
           (sut/positive (coercion.result/raw-value->success 0))))))

(deftest test-empty

  (testing "Valid coercion with nil"
    (is (= (coercion.result/success nil nil)
           (sut/empty (coercion.result/raw-value->success nil)))))

  (testing "Valid coercion with empty string"
    (is (= (coercion.result/success "" nil)
           (sut/empty (coercion.result/raw-value->success "")))))

  (testing "Failed coercion"
    (is (= (coercion.result/failure 1 sut/not-empty)
           (sut/empty (coercion.result/raw-value->success 1))))))

(deftest test-string

  (testing "Valid coercion with string"
    (is (= (coercion.result/success "foo" "foo")
           (sut/string (coercion.result/raw-value->success "foo"))))))

(deftest test-not-min-length

  (testing "Valid coercion with string"
    (is (= (coercion.result/success "foo" "foo")
           ((sut/min-length 3) (coercion.result/raw-value->success "foo")))))

  (testing "Invalid coercion with string"
    (is (= (coercion.result/failure "foo" sut/not-min-length)
           ((sut/min-length 5) (coercion.result/raw-value->success "foo")))))

  (testing "Invalid coercion with number"
    (is (= (coercion.result/failure 123 sut/not-min-length)
           ((sut/min-length 0) (coercion.result/raw-value->success 123))))))

(deftest test-tags

  (testing "Valid coercion with valid tags"
    (is (= (coercion.result/success ["foo" "bar" ""] ["foo" "bar"])
           (sut/tags (coercion.result/raw-value->success ["foo" "bar" ""])))))

  (testing "Invalid coercion with an invalid tag"
    (is (= (coercion.result/failure ["foo bar"] sut/not-valid-tags)
           (sut/tags (coercion.result/raw-value->success ["foo bar"]))))))

(deftest test-is-in

  (testing "Valid coercion with a value that is in the set"
    (let [in (coercion.result/success "foo" "foo")]
      (is (= in ((sut/is-in ["bar" "foo"]) in)))))

  (testing "Invalid coercion with a value that is not the set"
    (let [in (coercion.result/success "baz" "baz")]
      (is (= (coercion.result/failure "baz" sut/not-in-acceptable-vals)
             ((sut/is-in ["bar" "foo"]) in))))))
