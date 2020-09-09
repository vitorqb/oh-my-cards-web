(ns ohmycards.web.common.cards.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.cards.core :as sut]
            [ohmycards.web.kws.card :as kws.card]))

(deftest test-from-http
  (is (= {kws.card/id         "id"
          kws.card/title      "title"
          kws.card/body       "body"
          kws.card/tags       ["A"]
          kws.card/created-at "2020-01-01"
          kws.card/updated-at "2020-01-02"
          kws.card/ref        1}
         (sut/from-http {:id "id"
                         :title "title"
                         :body "body"
                         :tags ["A"]
                         :createdAt "2020-01-01"
                         :updatedAt "2020-01-02"
                         :ref 1}))))

(deftest test-to-http

  (testing "Base"
    (is (= {:id "id" :title "title" :body "body" :tags ["A"]}
           (sut/to-http {kws.card/id    "id"
                         kws.card/title "title"
                         kws.card/body  "body"
                         kws.card/tags  ["A"]}))))

  (testing "Removes empty tags"
    (is (= ["A"] (-> {kws.card/tags ["" "A" ""]} sut/to-http :tags)))))
