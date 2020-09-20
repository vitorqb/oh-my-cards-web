(ns ohmycards.web.services.cards-grid-profile-manager.route-sync-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.services.cards-grid-profile-manager.core :as kws]
            [ohmycards.web.services.cards-grid-profile-manager.core :as core]
            [ohmycards.web.services.cards-grid-profile-manager.route-sync :as sut]
            [ohmycards.web.services.events-bus.core :as events-bus]
            [ohmycards.web.services.routing.core :as services.routing]))

(deftest test-handle-success

  (let [log-calls (atom [])
        current-profile (atom nil)
        send-calls (atom [])
        response {kws/success? true kws/fetched-profile {:id 1}}]

    (with-redefs [sut/log #(swap! log-calls conj %&)
                  sut/current-profile current-profile
                  events-bus/send! #(swap! send-calls conj %&)]

      (sut/handle-success! response)

      (testing "Set's current-profile"
        (is (= @current-profile {:id 1})))

      (testing "Sends msg to bus"
        (is (= [kws/action-new-grid-profile {:id 1}]))))))

(deftest test-handle-error

  (let [log-calls (atom [])
        current-profile (atom nil)
        send-calls (atom [])
        response {kws/success? true kws/fetched-profile {:id 1}}]

    (with-redefs [sut/log #(swap! log-calls conj %&)
                  sut/current-profile current-profile
                  events-bus/send! #(swap! send-calls conj %&)]

      (sut/handle-error! response)

      (testing "Set's current-profile to nil"
        (is (= @current-profile nil)))

      (testing "Sends msg to bus"
        (is (= [kws/action-new-grid-profile nil]))))))


(deftest test-load-from-route-change

  (testing "When there is no profile on the route => loads with nil"
    (let [load-calls (atom [])]
      (with-redefs [sut/load! #(swap! load-calls conj %&)]
        (let [old-match {:query-params {:grid-profile "FOO"}}
              new-match {:query-params {:grid-profile nil}}]
          (sut/load-from-route-change! old-match new-match)
          (is (= [[nil]] @load-calls))))))

  (testing "When the profile hasn't changed, do nothing"
    (let [load-calls (atom [])]
      (with-redefs [sut/load! #(swap! load-calls conj %&)]
        (let [old-match {:query-params {:grid-profile "FOO"}}
              new-match {:query-params {:grid-profile "FOO"}}]
          (sut/load-from-route-change! old-match new-match)
          (is (= [] @load-calls))))))

  (testing "When the profile has changed, loads and return"
    (let [load-calls (atom [])]
      (with-redefs [sut/load! #(do (swap! load-calls conj %&)
                                   ::result)]
        (let [old-match {:query-params {:grid-profile "FOO"}}
              new-match {:query-params {:grid-profile "BAR"}}
              result    (sut/load-from-route-change! old-match new-match)]
          (is (= ::result result))
          (is (= [["BAR"]] @load-calls)))))))

(deftest test-set-in-route!

  (testing "Update grid-profile in query params"
    (let [args (atom [])]
      (with-redefs [services.routing/update-query-params! #(swap! args conj %&)]
        (sut/set-in-route! "name")
        (is (= [[{:grid-profile "name"}]] @args))))))
