(ns ohmycards.web.common.async-actions.core-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.async-actions.core :as sut]
            [ohmycards.web.kws.common.async-actions.core :as kws]))

(deftest is-channel?
  (is (true? (sut/is-channel? (a/go nil))))
  (is (true? (sut/is-channel? (a/timeout 1000))))
  (is (false? (sut/is-channel? 1)))
  (is (false? (sut/is-channel? "foo")))
  (is (false? (sut/is-channel? {}))))

(deftest test-run--simple
  (let [async-action {kws/state (atom {})
                      kws/pre-reducer-fn (fn [s] (assoc s ::pre-hook-fn 1))
                      kws/post-reducer-fn (fn [s r] (assoc s ::post-hook-fn r))
                      kws/action-fn (fn [] (a/go ::result))}]
    (async done
     (a/go
       (a/<! (sut/run async-action))
       (is (= {::pre-hook-fn 1 ::post-hook-fn ::result}
              @(kws/state async-action)))
       (done)))))

(deftest test-run--pre-and-post-hooks-called
  (let [hooks-called (atom [])
        async-action {kws/state (atom {})
                      kws/pre-hook-fn (fn [] (swap! hooks-called conj [::pre-hook]))
                      kws/post-hook-fn (fn [r] (swap! hooks-called conj [::post-hook r]))
                      kws/action-fn (fn [] (a/go ::result))}]
    (async done
     (a/go
       (a/<! (sut/run async-action))
       (is (= [[::pre-hook] [::post-hook ::result]]
              @hooks-called))
       (done)))))

(deftest test-run--action-fn-called-with-state
  (let [calls (atom [])
        async-action {kws/state (atom {::foo 1})
                      kws/action-fn (fn [s] (do (swap! calls conj [s])
                                                (a/go ::result)))}]
    (async done
     (a/go
       (a/<! (sut/run async-action))
       (is (= [[{::foo 1}]] @calls))
       (done)))))

(deftest test-run--action-not-called-if-condition-fn-returns-chan-with-false
  (let [calls (atom 0)
        async-action {kws/state (atom {})
                      kws/run-condition-fn #(a/go nil)
                      kws/action-fn (fn [_] (swap! calls inc))}]
    (async done
     (a/go
       (a/<! (sut/run async-action))
       (is (= 0 @calls))
       (done)))))

(deftest test-run--action-not-called-if-condition-fn-returns-false
  (let [calls (atom 0)
        async-action {kws/state (atom {})
                      kws/run-condition-fn (constantly false)
                      kws/action-fn (fn [_] (swap! calls inc))}]
    (async done
     (a/go
       (a/<! (sut/run async-action))
       (is (= 0 @calls))
       (done)))))

(deftest test-run--return-value
  (let [async-action {kws/state (atom {:val 2})
                      kws/action-fn (fn [_] {:val 1})
                      kws/return-value-fn (fn [response state] (+ (:val response) (:val state)))}]
    (async done
           (a/go
             (let [result (a/<! (sut/run async-action))]
               (is (= 3 result))
               (done))))))
