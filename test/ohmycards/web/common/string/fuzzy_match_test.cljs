(ns ohmycards.web.common.string.fuzzy-match-test
  (:require [ohmycards.web.common.string.fuzzy-match :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-main

  (testing "No matches"
    (is (= [] (sut/main "FOO" ["bar" "Baz" "BOZ"]))))

  (testing "Exact match"
    (is (= ["FOO"] (sut/main "FOO" ["bar" "FOO" "Baz" "BOZ"]))))

  (testing "Exact match case insensitive"
    (is (= ["FOO"] (sut/main "Foo" ["bar" "FOO" "Baz" "BOZ"]))))

  (testing "Begins with"
    (is (= ["FOO" "FOOBAR"] (sut/main "FOO" ["bar" "FOO" "FOOBAR" "Baz" "BOZ"])))
    (is (= ["bar" "FOOBAR" "Baz" "BOZ"] (sut/main "b" ["bar" "FOO" "FOOBAR" "Baz" "BOZ"]))))

  (testing "Ends with"
    (is (= ["bar" "FOOBAR"] (sut/main "AR" ["bar" "FOO" "FOOBAR" "Baz" "BOZ"]))))

  (testing "Space matches anything"
    (is (= ["foo bar" "foo and bar"] (sut/main "foo bar" ["foo" "bar" "foo bar" "foo and bar"]))))

  (testing "Match with special chars"
    (is (= ["foo!@#$%^&*()[]{}"] (sut/main "foo!@#$%^&*()[]{}" ["foo!@#$%^&*()[]{}"])))))
