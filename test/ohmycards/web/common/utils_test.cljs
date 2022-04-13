(ns ohmycards.web.common.utils-test
  (:require [ohmycards.web.common.utils :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.globals :as globals]))

(deftest test-to-path
  (is (= [:k] (sut/to-path :k)))
  (is (= [:k] (sut/to-path [:k])))
  (is (= ["FOO"] (sut/to-path "FOO")))
  (is (nil? (sut/to-path {}))))

(deftest test-str->number

  (testing "Empty"
    (is (= nil (sut/str->number ""))))

  (testing "Invalid with string"
    (is (= nil (sut/str->number "foo"))))

  (testing "Invalid with dots"
    (is (= nil (sut/str->number "."))))

  (testing "Invalid with too many dots"
    (is (= nil (sut/str->number "1.1.1"))))

  (testing "Invalid with trailing space"
    (is (= nil (sut/str->number "2 "))))

  (testing "Valid decimal"
    (is (= 1.1 (sut/str->number "1.1"))))

  (testing "Valid with dot as first char"
    (is (= 0.21 (sut/str->number ".21"))))

  (testing "Valid integer"
    (is (= 2 (sut/str->number "2"))))

  (testing "Valid negative integer"
    (is (= -2 (sut/str->number "-2"))))

  (testing "Valid positive integer"
    (is (= 2 (sut/str->number "+2")))))

(deftest test-internal-link*

  (testing "No port"
    (is (= "www.url.com/foo" (sut/internal-link* {:hostname "www.url.com"} "/foo"))))

  (testing "With port"
    (is (= "www.url.com:3000/foo" (sut/internal-link* {:hostname "www.url.com" :port 3000}
                                                      "/foo")))))
