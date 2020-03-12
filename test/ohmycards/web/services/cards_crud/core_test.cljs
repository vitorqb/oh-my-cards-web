(ns ohmycards.web.services.cards-crud.core-test
  (:require [ohmycards.web.services.cards-crud.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.services.cards-crud.core :as kws]))

(deftest test-parse-response

  (testing "Error -> sets error message"
    (is (= {kws/error-message "errmsg"}
           (sut/parse-response {kws.http/success? false kws.http/body "errmsg"}))))

  (testing "Success -> returns created card"
    (is (= {kws/created-card {kws.card/id "ID"
                              kws.card/body "BODY"
                              kws.card/title "TITLE"}}
           (sut/parse-response {kws.http/success? true
                                kws.http/body {:id "ID"
                                               :body "BODY"
                                               :title "TITLE"}})))))

(deftest test-run-create-call!

  (testing "Calls http-fn with correct args"
    (is
     (=
      {kws.http/method :POST
       kws.http/url "/v1/cards"
       kws.http/json-params {:title "FOO" :body "BAR"}}
      (sut/run-create-call! {:http-fn hash-map} {kws.card/title "FOO" kws.card/body "BAR"})))))
