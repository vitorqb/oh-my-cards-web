(ns ohmycards.web.services.cards-crud.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-crud.actions :as kws.actions]
            [ohmycards.web.kws.services.cards-crud.core :as kws]
            [ohmycards.web.services.cards-crud.core :as sut]))

(deftest test-parse-create-response

  (testing "Error -> sets error message"
    (is (= {kws/error-message "errmsg"}
           (sut/parse-response*
            kws.actions/create
            {kws.http/success? false kws.http/body "errmsg"}))))

  (testing "Success -> returns created card"
    (is (= {kws/created-card {kws.card/id    "ID"
                              kws.card/body  "BODY"
                              kws.card/title "TITLE"
                              kws.card/tags  ["TAG"]}}
           (sut/parse-response*
            kws.actions/create
            {kws.http/success? true
             kws.http/body     {:id    "ID"
                                :body  "BODY"
                                :title "TITLE"
                                :tags  ["TAG"]}})))))

(deftest test-parse-read-response

  (testing "Error -> sets error message"
    (is (= {kws/error-message "errmsg"}
           (sut/parse-response* kws.actions/read {kws.http/success? false kws.http/body "errmsg"}))))

  (testing "Success -> returns read card"
    (is (= {kws/read-card {kws.card/id "1"
                           kws.card/body "2"
                           kws.card/title "3"
                           kws.card/tags ["4"]}}
           (sut/parse-response* kws.actions/read {kws.http/success? true
                                                  kws.http/body {:id "1"
                                                                 :body "2"
                                                                 :title "3"
                                                                 :tags ["4"]}})))))

(deftest test-parse-update-response

  (testing "Success"

    (testing "Assocs updated-card"
      (is (= (sut/http-body->card {:id 1})
             (kws/updated-card
              (sut/parse-response* kws.actions/update {kws.http/success? true
                                                       kws.http/body {:id 1}}))))))

  (testing "Failure"

    (testing "Set's error-msg"
      (is (= "err" (kws/error-message
                    (sut/parse-response* kws.actions/update {kws.http/success? false
                                                            kws.http/body "err"})))))))

(deftest test-run-update-call!
  (is (= {kws.http/method :POST
          kws.http/url "/v1/cards/foo"
          kws.http/json-params {:body "body" :title "title" :tags ["A"]}}
         (sut/run-http-call!* kws.actions/update {:http-fn hash-map
                                                 kws/card-input {kws.card/id "foo"
                                                                 kws.card/body "body"
                                                                 kws.card/title "title"
                                                                 kws.card/tags ["A"]}}))))

(deftest test-run-create-call!

  (testing "Calls http-fn with correct args"
    (is
     (=
      {kws.http/method :POST
       kws.http/url "/v1/cards"
       kws.http/json-params {:title "FOO" :body "BAR" :tags ["A"]}}
      (sut/run-http-call!* kws.actions/create
                          {:http-fn hash-map
                           kws/card-input {kws.card/title "FOO"
                                           kws.card/body "BAR"
                                           kws.card/tags ["A" ""]}})))))

(deftest test-run-read-call!
  (is (= {kws.http/method :GET kws.http/url "/v1/cards/1"}
         (sut/run-http-call!* kws.actions/read {:http-fn hash-map kws/card-id "1"}))))

(deftest test-run-delete-call!
  (is (= {kws.http/method :DELETE kws.http/url "/v1/cards/1"}
         (sut/run-http-call!* kws.actions/delete {:http-fn hash-map kws/card-id "1"}))))

(deftest test-parse-delete-response

  (testing "Success"
    (let [http-resp {kws.http/success? true}
          resp (sut/parse-response* kws.actions/delete http-resp)]

      (is (= {} resp))))

  (testing "Failure"
    (let [http-resp {kws.http/success? false}
          resp (sut/parse-response* kws.actions/delete http-resp)]

      (testing "Sets error-message"
        (is (= "Error deleting card!" (kws/error-message resp))))))

  (testing "Failure"
    (let [http-resp {kws.http/success? false kws.http/body "ERR"}
          resp (sut/parse-response* kws.actions/delete http-resp)]

      (testing "Assocs error-message"
        (is (= "ERR" (kws/error-message resp)))))))
