(ns ohmycards.web.components.dialog.core-test
  (:require [ohmycards.web.components.dialog.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.components.dialog.core :as kws]
            [ohmycards.web.test-utils :as tu]))

(deftest test-main

  (letfn [(gen-state [& {:as xs}] (atom (merge {kws/active? true} xs)))]

    (testing "nil when not active"
      (is (= nil (sut/main {:state (gen-state kws/active? false)}))))

    (testing "Renders children inside body if active"
      (is (tu/exists-in-component?
           [:div.dialog__body ::child1 ::child2]
           (sut/main {:state (gen-state)} ::child1 ::child2))))

    (testing "Renders header inside contents if active"
      (let [props {:state (gen-state)}]
        (is (tu/exists-in-component? [sut/header props] (sut/main props)))))))


(deftest test-show!

  (testing "Set's active to true"
    (let [state (atom {kws/active? false})
          props {:state state}]
      (sut/show! props)
      (is (true? (kws/active? @state)))))

  (testing "Calls on-show!"
    (let [calls (atom 0)
          props {kws/on-show! #(swap! calls inc) :state (atom {})}]
      (sut/show! props)
      (is (= 1 @calls)))))

(deftest test-hide!

  (testing "Set's active to false"
    (let [state (atom {kws/active? true})
          props {:state state}]
      (sut/hide! props)
      (is (false? (kws/active? @state)))))

  (testing "Calls on-hide!"
    (let [calls (atom 0)
          props {kws/on-hide! #(swap! calls inc) :state (atom {})}]
      (sut/hide! props)
      (is (= 1 @calls)))))
