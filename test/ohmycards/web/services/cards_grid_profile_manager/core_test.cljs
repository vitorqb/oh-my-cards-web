(ns ohmycards.web.services.cards-grid-profile-manager.core-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.metadata.core :as kws.cards-grid.metadata]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.kws.services.events-bus.core :as events-bus]
            [ohmycards.web.services.cards-grid-profile-manager.core :as sut]
            [ohmycards.web.services.cards-grid-profile-manager.impl.fetch-metadata
             :as
             fetch-metadata]))

(deftest test-fetch-metadata!*__base
  (let [fetch-response {kws.http/body {:names ["FOO" "BAR"]}}
        parsed-response (fetch-metadata/parse-response fetch-response)
        do-fetch-metadata! #(a/go parsed-response)
        set-metadata-fn! #(assoc % ::foo 1)
        opts {kws/set-metadata-fn! set-metadata-fn!}
        response-chan (sut/fetch-metadata!* opts do-fetch-metadata!)]
    (async done
           (a/go
             (is (= {kws.cards-grid.metadata/profile-names ["FOO" "BAR"] ::foo 1}
                    (a/<! response-chan)))
             (done)))))
