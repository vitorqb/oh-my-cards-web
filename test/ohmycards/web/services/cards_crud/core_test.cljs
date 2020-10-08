(ns ohmycards.web.services.cards-crud.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.cards.core :as common.cards]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-crud.actions :as kws.actions]
            [ohmycards.web.kws.services.cards-crud.core :as kws]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.protocols.http :as protocols.http]
            [ohmycards.web.services.cards-crud.core :as sut]))

(deftest test-create-action

  (let [card-input {kws.card/title "FOO" kws.card/body "BAR" kws.card/tags ["A" ""]}
        action (sut/->CreateAction card-input)]

    (testing "Returns correct url"
      (is (= "/v1/cards" (protocols.http/url action))))

    (testing "Returns correct method"
      (is (= :POST (protocols.http/method action))))

    (testing "Returns card input as http for json params"
      (is (= {:title "FOO" :body "BAR" :tags ["A"]}
             (protocols.http/json-params action))))

    (testing "Parses success responses"
      (let [response {kws.http/body {:id "ID"
                                     :body "BODY"
                                     :title "TITLE"
                                     :tags ["TAG"]
                                     :ref 1}}
            card {kws.card/id         "ID"
                  kws.card/body       "BODY"
                  kws.card/title      "TITLE"
                  kws.card/tags       ["TAG"]
                  kws.card/created-at nil
                  kws.card/updated-at nil
                  kws.card/ref        1}]
        (is (= {kws/created-card card}
               (protocols.http/parse-success-response action response)))))

    (testing "Parses error responses"
      (let [response {kws.http/success? false kws.http/body "errmsg"}]
        (is (= {kws/error-message "errmsg"}
               (protocols.http/parse-error-response action response)))))

    (testing "Sends event to bus after response"
      (with-redefs [events-bus/send! #(do [::send %1 %2])]
        (is (= [::send kws.actions/create ::parsed-response]
               (protocols.http/do-after! action ::response ::parsed-response)))))))

(deftest test-read-action

  (let [action (sut/->ReadAction "id")]

    (testing "Returns correct url"
      (is (= "/v1/cards/id" (protocols.http/url action))))

    (testing "Returns correct method"
      (is (= :GET (protocols.http/method action))))

    (testing "Parses success responses"
      (let [response {kws.http/body {:id "id"
                                     :body "BODY"
                                     :title "TITLE"
                                     :tags ["TAG"]
                                     :ref 1}}
            card {kws.card/id         "id"
                  kws.card/body       "BODY"
                  kws.card/title      "TITLE"
                  kws.card/tags       ["TAG"]
                  kws.card/created-at nil
                  kws.card/updated-at nil
                  kws.card/ref        1}]
        (is (= {kws/read-card card}
               (protocols.http/parse-success-response action response)))))

    (testing "Parses error responses"
      (let [response {kws.http/success? false kws.http/body "errmsg"}]
        (is (= {kws/error-message "errmsg"}
               (protocols.http/parse-error-response action response)))))

    (testing "Sends event to bus after response"
      (with-redefs [events-bus/send! #(do [::send %1 %2])]
        (is (= [::send kws.actions/read ::parsed-response]
               (protocols.http/do-after! action ::response ::parsed-response)))))))

(deftest test-update-action

  (let [card-input {kws.card/id "id"
                    kws.card/title "FOO"
                    kws.card/body "BAR"
                    kws.card/tags ["A" ""]}
        action (sut/->UpdateAction card-input)]

    (testing "Returns correct url"
      (is (= "/v1/cards/id" (protocols.http/url action))))

    (testing "Returns correct method"
      (is (= :POST (protocols.http/method action))))

    (testing "Returns card input as http for json params"
      (is (= {:id "id" :title "FOO" :body "BAR" :tags ["A"]}
             (protocols.http/json-params action))))

    (testing "Parses success responses"
      (let [response {kws.http/body {:id "ID"
                                     :body "BODY"
                                     :title "TITLE"
                                     :tags ["TAG"]
                                     :ref 1}}
            card {kws.card/id         "ID"
                  kws.card/body       "BODY"
                  kws.card/title      "TITLE"
                  kws.card/tags       ["TAG"]
                  kws.card/created-at nil
                  kws.card/updated-at nil
                  kws.card/ref        1}]
        (is (= {kws/updated-card card}
               (protocols.http/parse-success-response action response)))))

    (testing "Parses error responses"
      (let [response {kws.http/success? false kws.http/body "errmsg"}]
        (is (= {kws/error-message "errmsg"}
               (protocols.http/parse-error-response action response)))))

    (testing "Sends event to bus after response"
      (with-redefs [events-bus/send! #(do [::send %1 %2])]
        (is (= [::send kws.actions/update ::parsed-response]
               (protocols.http/do-after! action ::response ::parsed-response)))))))

(deftest test-delete-action

  (let [action (sut/->DeleteAction "id")]

    (testing "Returns correct url"
      (is (= "/v1/cards/id" (protocols.http/url action))))

    (testing "Returns correct method"
      (is (= :DELETE (protocols.http/method action))))

    (testing "Parses success responses"
      (let [response {}]
        (is (= {} (protocols.http/parse-success-response action response)))))

    (testing "Parses error responses"
      (let [response {kws.http/success? false kws.http/body "errmsg"}]
        (is (= {kws/error-message "errmsg"}
               (protocols.http/parse-error-response action response)))))

    (testing "Sends event to bus after response"
      (with-redefs [events-bus/send! #(do [::send %1 %2])]
        (is (= [::send kws.actions/delete ::parsed-response]
               (protocols.http/do-after! action ::response ::parsed-response)))))))
