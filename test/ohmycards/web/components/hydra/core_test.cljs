(ns ohmycards.web.components.hydra.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.inputs.simple :as inputs.simple]
            [ohmycards.web.components.hydra.core :as sut]
            [ohmycards.web.kws.components.hydra.core :as kws]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.test-utils :as tu]))

(deftest test-state-watcher-for-leaf-selection

  (testing "Calls on-leaf-selection if new leaf is selected"
    (let [new-state         {kws/path "f"}
          leaf              {kws.hydra/type kws.hydra/leaf kws.hydra/shortcut \f}
          head              {kws.hydra/type kws.hydra/branch kws.hydra.branch/heads [leaf]}
          on-leaf-selection #(do [::selection %])
          props             {kws/head head kws/on-leaf-selection on-leaf-selection}]
      (is (= [::selection leaf]
             ((sut/state-watcher-for-leaf-selection props) nil nil nil new-state))))))

(deftest test-head

  (testing "Renders the shortcut and description for each head"
    (let [head1 {kws.hydra/shortcut \f
                 kws.hydra/description "Foo"}
          head2 {kws.hydra/shortcut \b
                 kws.hydra/description "Bar"}
          heads [head1 head2]
          branch {kws.hydra/type kws.hydra/branch
                  kws.hydra.branch/heads [head1 head2]}]
      (is (tu/exists-in-component? [:div "[f]: Foo"]
                                   (sut/head-component {::sut/head branch})))
      (is (tu/exists-in-component? [:div "[b]: Bar"]
                                   (sut/head-component {::sut/head branch}))))))

(deftest test-main

  (letfn [(gen-props [& {:as xs}] (merge {:state (atom (merge {} xs))}))]

    (testing "Renders the main input"
      (let [props       (gen-props kws/path "a")
            input-props (tu/get-props-for inputs.simple/main (sut/main props))]
        (is (= "a" (:value input-props)))
        (is (fn? (:on-change input-props)))
        (is (= "hydra__input" (:class input-props)))))

    (testing "Renders the root head"
      (let [head {kws.hydra/description "Foo"}
            props (assoc (gen-props) kws/head head)]
        (is (tu/exists-in-component? [sut/head-component (assoc props ::sut/head head)]
                                     (sut/main props)))))))
