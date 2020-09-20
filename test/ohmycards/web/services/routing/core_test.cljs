(ns ohmycards.web.services.routing.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.services.routing.core :as kws]
            [ohmycards.web.services.events-bus.core :as event-bus]
            [ohmycards.web.services.routing.core :as sut]
            [reitit.frontend :as rf]))

(def routes
  "Routes used for testing"
  [["/" {}
    ["" {:name :home}]
    ["foo" {:name :foo}]
    ["bar/:id" {:name :bar}]]])

(deftest test-do-route!

  (with-redefs [event-bus/send! #(do)
                sut/run-view-hooks! #(do)]

    (let [match {:name ::new-match}]

      (testing "Resets routing-state to new match"
        (let [routing-state (atom nil)]
          (sut/do-route! routing-state match)
          (is (= @routing-state match))))

      (testing "Sends event to the bus"
        (with-redefs [event-bus/send! #(do [::send! %1 %2])]
          (is (= [::send! kws/action-navigated-to-route [nil match]]
                 (sut/do-route! (atom nil) match))))))))

(deftest test-new-query-params

  (testing "Updates keeping from `global` and adding `update`"
    (let [current-match {:query-params {:foo 1 :bar 2}}
          opts {kws/global-query-params #{:foo}}
          new-params-map {:bar 3 :baz 4}]
      (is (= {:foo 1 :bar 3 :baz 4}
             (sut/new-query-params current-match opts new-params-map)))))

  (testing "Remove all if not global and no update"
    (let [current-match {:query-params {:foo 1 :bar 2}}
          opts {kws/global-query-params #{}}
          new-params-map {}]
      (is (= {} (sut/new-query-params current-match opts new-params-map)))))

  (testing "Keep only global"
    (let [current-match {:query-params {:foo 1 :bar 2}}
          opts {kws/global-query-params #{:bar}}
          new-params-map {}]
      (is (= {:bar 2} (sut/new-query-params current-match opts new-params-map)))))

  (testing "Updates global"
    (let [current-match {:query-params {:foo 1 :bar 2}}
          opts {kws/global-query-params #{:bar}}
          new-params-map {:bar 3}]
      (is (= {:bar 3} (sut/new-query-params current-match opts new-params-map))))))

(deftest test-update-query-params!
  (testing "Calls goto! with updated query params"
    (let [goto-args (atom [])]
      (with-redefs [sut/goto! #(swap! goto-args conj %&)
                    sut/*state* (atom {:data {kws/name :foo} :query-params {:foo "1" :bar "2"}})]
        (sut/update-query-params! {:foo "2" :baz "3"})
        (is (= [[:foo kws/query-params {:foo "2" :bar "2" :baz "3"}]] @goto-args))))))

(deftest test-run-views-hooks!

  (testing "If both matches have the same name, runs update hook"

    (let [old-match {::foo 1 :data {:name ::name}}
          new-match {::foo 2 :data {:name ::name}}
          args (atom [])]

      (with-redefs [sut/run-hook! #(swap! args conj [%1 %2])]
        (sut/run-view-hooks! old-match new-match)
        (is (= [[kws/update-hook new-match]] @args)))))

  (testing "If matches do not have the same name, runs exit and then enter hooks"
    (let [old-match {:data {:name ::old}}
          new-match {:data {:name ::new}}
          args (atom [])]

      (with-redefs [sut/run-hook! #(swap! args conj [%1 %2])]
        (sut/run-view-hooks! old-match new-match)
        (is (= [[kws/exit-hook old-match] [kws/enter-hook new-match]] @args))))))

(deftest test-path-to!

  (testing "No global params"
    (with-redefs [sut/*state* (atom nil)
                  sut/*opts*  {}]
      (is (= "/#/" (sut/path-to! :home {} (rf/router routes))))
      (is (= "/#/foo" (sut/path-to! :foo {} (rf/router routes))))
      (is (= "/#/foo?id=1" (sut/path-to! :foo {:query-params {:id 1}} (rf/router routes))))
      (is (= "/#/bar/1?id=2" (sut/path-to! :bar {:query-params {:id 2} :path-params {:id 1}}
                                           (rf/router routes))))))

  (testing "With global params"
    (with-redefs [sut/*state* (atom {:query-params {:foo "1" :bar "2"}})
                  sut/*opts*  {kws/global-query-params #{:foo}}]
      (is (= "/#/?foo=1" (sut/path-to! :home {} (rf/router routes))))
      (is (= "/#/foo?foo=1" (sut/path-to! :foo {} (rf/router routes))))
      (is (= "/#/foo?foo=1&id=1" (sut/path-to! :foo {:query-params {:id 1}} (rf/router routes))))
      (is (= "/#/bar/1?foo=1&id=2" (sut/path-to! :bar {:query-params {:id 2} :path-params {:id 1}}
                                                 (rf/router routes))))
      (is (= "/#/bar/1?foo=2" (sut/path-to! :bar {:query-params {:foo 2} :path-params {:id 1}}
                                            (rf/router routes)))))))
