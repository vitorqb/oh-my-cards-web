(ns ohmycards.web.components.dialog.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.dialog.core :as sut]
            [ohmycards.web.kws.components.dialog.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [reagent.core :as r]))

(defn- mk-state [& {:as xs}]
  (atom (merge {kws/active? true} xs)))

(defn- mk-props
  ([] (mk-props nil))
  ([{::kws/keys [active? on-show! on-hide!]
     :or {active? true
          on-show! (constantly nil)
          on-hide! (constantly nil)}}]
   {:state (r/atom {kws/active? active?})
    kws/on-hide! on-hide!
    kws/on-show! on-show!}))

(deftest test-main

  (testing "nil when not active"
    (is (nil? (sut/main (mk-props {kws/active? false})))))

  (testing "Renders children inside body if active"
    (is (tu/exists-in-component?
         [:div.dialog__body ::child1 ::child2]
         (sut/main (mk-props) ::child1 ::child2))))

  (testing "Renders header inside contents if active"
    (let [props (mk-props)
          comp (sut/main props)]
      (is (tu/exists-in-component? [sut/header props] comp)))))

(deftest test-is-visible?
  (let [props (mk-props)]
    (is (true? (sut/is-visible? props)))
    (sut/hide! props)
    (is (false? (sut/is-visible? props)))
    (sut/show! props)
    (is (true? (sut/is-visible? props)))))

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
