(ns ohmycards.web.services.cards-grid-profile-manager.impl.update-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.services.cards-grid-profile-manager.impl.helpers
             :as
             helpers]
            [ohmycards.web.services.cards-grid-profile-manager.impl.update :as sut]))

(deftest test-run-http-call
  (with-redefs [helpers/serialize-profile identity]
    (let [profile {kws.profile/name "FOO"}
          http-fn hash-map
          opts {:http-fn http-fn}]
      (is (= {kws.http/method :POST
              kws.http/url "/v1/cards-grid-profile/FOO"
              kws.http/json-params profile}
             (sut/run-http-call! opts profile))))))

(deftest test-main!
  (async
   done
   (let [http-resp {kws.http/success? true}]
     (with-redefs [sut/run-http-call! #(a/go http-resp)]
       (let [opts {}
             profile ""
             result-chan (sut/main! opts profile)
             exp-result {kws/success? true}]
         (a/go
           (is (= exp-result (a/<! result-chan)))
           (done)))))))
