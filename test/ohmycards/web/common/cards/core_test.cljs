(ns ohmycards.web.common.cards.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.cards.core :as sut]
            [ohmycards.web.kws.card :as kws.card]))

(deftest test-from-http
  (is (= {kws.card/id    "id"
          kws.card/title "title"
          kws.card/body  "body"
          kws.card/tags  ["A"]}
         (sut/from-http {:id "id" :title "title" :body "body" :tags ["A"]}))))

(deftest test-to-http

  (testing "Base"
    (is (= {:id "id" :title "title" :body "body" :tags ["A"]}
           (sut/to-http {kws.card/id    "id"
                         kws.card/title "title"
                         kws.card/body  "body"
                         kws.card/tags  ["A"]}))))

  (testing "Removes empty tags"
    (is (= ["A"] (-> {kws.card/tags ["" "A" ""]} sut/to-http :tags)))))
