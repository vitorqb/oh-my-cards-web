(ns ohmycards.web.services.http-test
  (:require [ohmycards.web.services.http :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.http :as kws.http]))

(deftest test-parse-args

  (testing "Base"
    (is (= {:method :post :url "/api/URL" :with-credentials? false}
           (sut/parse-args {::kws.http/method :post
                            ::kws.http/url "/URL"}))))

  (testing "Adds json-params"
    (is (= {:method :post :url "/api/URL" :with-credentials? false :json-params {::foo 1}}
           (sut/parse-args {::kws.http/method :post
                            ::kws.http/url "/URL"
                            ::kws.http/json-params {::foo 1}}))))

  (testing "Adds query-params"
    (is (= {:method :get :url "/api/URL" :with-credentials? false :query-params {::foo 1}}
           (sut/parse-args {::kws.http/method :get
                            ::kws.http/url "/URL"
                            ::kws.http/query-params {::foo 1}}))))

  (testing "Adds multipart-params"
    (is (= {:method :get :url "/api/URL" :with-credentials? false :multipart-params [["A" "B"]]}
           (sut/parse-args {::kws.http/method :get
                            ::kws.http/url "/URL"
                            ::kws.http/multipart-params [["A" "B"]]}))))

  (testing "Adds token"
    (is (= {:method :post
            :url "/api/URL"
            :with-credentials? false
            :headers {"Authorization" "Bearer FOO"}}
           (sut/parse-args {::kws.http/method :post
                            ::kws.http/url "/URL"
                            ::kws.http/token "FOO"})))))

(deftest test-parse-response

  (testing "Base"
    (is (= {::kws.http/success? true
            ::kws.http/body {:foo :bar}
            ::kws.http/status 200}
           (sut/parse-response {:success true
                                :body {:foo :bar}
                                :status 200})))))
