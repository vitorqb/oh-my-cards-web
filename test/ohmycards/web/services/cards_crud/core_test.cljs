(ns ohmycards.web.services.cards-crud.core-test
  (:require [ohmycards.web.services.cards-crud.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.services.cards-crud.core :as kws]))

(deftest test-parse-create-response

  (testing "Error -> sets error message"
    (is (= {kws/error-message "errmsg"}
           (sut/parse-create-response {kws.http/success? false kws.http/body "errmsg"}))))

  (testing "Success -> returns created card"
    (is (= {kws/created-card {kws.card/id "ID"
                              kws.card/body "BODY"
                              kws.card/title "TITLE"}}
           (sut/parse-create-response {kws.http/success? true
                                       kws.http/body {:id "ID"
                                                      :body "BODY"
                                                      :title "TITLE"}})))))

(deftest test-parse-read-response

  (testing "Error -> sets error message"
    (is (= {kws/error-message "errmsg"}
           (sut/parse-read-response {kws.http/success? false kws.http/body "errmsg"}))))

  (testing "Success -> returns read card"
    (is (= {kws/read-card {kws.card/id "1" kws.card/body "2" kws.card/title "3"}}
           (sut/parse-read-response {kws.http/success? true
                                     kws.http/body {:id "1":body "2" :title "3"}})))))

(deftest test-run-create-call!

  (testing "Calls http-fn with correct args"
    (is
     (=
      {kws.http/method :POST
       kws.http/url "/v1/cards"
       kws.http/json-params {:title "FOO" :body "BAR"}}
      (sut/run-create-call! {:http-fn hash-map} {kws.card/title "FOO" kws.card/body "BAR"})))))

(deftest test-run-read-call!
  (is (= {kws.http/method :GET kws.http/url "/v1/cards/1"}
         (sut/run-read-call! {:http-fn hash-map} "1"))))

(deftest test-run-delete-call!
  (is (= {kws.http/method :DELETE kws.http/url "/v1/cards/1"}
         (sut/run-delete-call! {:http-fn hash-map} "1"))))

(deftest test-parse-delete-response

  (testing "Success"
    (let [http-resp {kws.http/success? true}
          resp (sut/parse-delete-response http-resp)]

      (is (= {} resp))))

  (testing "Failure"
    (let [http-resp {kws.http/success? false}
          resp (sut/parse-delete-response http-resp)]

      (testing "Sets error-message"
        (is (= "Error deleting card!" (kws/error-message resp))))))

  (testing "Failure"
    (let [http-resp {kws.http/success? false kws.http/body "ERR"}
          resp (sut/parse-delete-response http-resp)]

      (testing "Assocs error-message"
        (is (= "ERR" (kws/error-message resp)))))))
