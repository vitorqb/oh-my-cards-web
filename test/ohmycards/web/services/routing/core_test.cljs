(ns ohmycards.web.services.routing.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.services.events-bus.core :as event-bus]
            [ohmycards.web.kws.services.routing.core :as kws]
            [ohmycards.web.services.routing.core :as sut]))

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
          (is (= [::send! kws/action-navigated-to-route match]
                 (sut/do-route! (atom nil) match)))))

      (testing "Calls run-view-hooks!"
        (let [args (atom nil)
              old-match {:name ::old-match}
              routing-state (atom old-match)]
          (with-redefs [sut/run-view-hooks! #(reset! args %&)]
            (sut/do-route! routing-state match)
            (is (= [old-match match] @args))))))))


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
