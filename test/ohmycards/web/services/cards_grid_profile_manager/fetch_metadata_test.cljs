(ns ohmycards.web.services.cards-grid-profile-manager.fetch-metadata-test
  (:require [ohmycards.web.services.cards-grid-profile-manager.fetch-metadata :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.cards-grid.metadata.core :as kws.cards-grid.metadata]))

(deftest test-parse-response
  (testing "Returns the names of the profiles."
    (let [response {kws.http/body {:names ["FOO"]}}]
      (is (= {kws.cards-grid.metadata/profile-names ["FOO"]}
             (sut/parse-response response))))))

(deftest test-run-http-call
  (testing "Calls http-fn with arguments"
    (is (= {kws.http/url "/v1/metadata/cards-grid-profile/names"
            kws.http/method "get"}
           (sut/run-http-call! {:http-fn hash-map})))))
