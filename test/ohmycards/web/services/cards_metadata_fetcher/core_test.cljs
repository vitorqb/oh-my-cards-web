(ns ohmycards.web.services.cards-metadata-fetcher.core-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.services.cards-metadata-fetcher.core :as sut]))

(deftest test-main

  (let [resp-chan (a/go {kws.http/body {:tags ["A" "B"]}})
        expected {kws.card-metadata/tags ["A" "B"]}]

    (letfn [(http-fn [& {::kws.http/keys [url method]}]
              (is (= sut/url url))
              (is (= :GET method))
              resp-chan)]

      (let [result-chan (sut/main {:http-fn http-fn})]

        (async
         done
         (a/go
           (is (= expected (a/<! result-chan)))
           (done)))))))
