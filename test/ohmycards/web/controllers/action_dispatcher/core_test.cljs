(ns ohmycards.web.controllers.action-dispatcher.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.action-dispatcher.core :as action-dispatcher]
            [ohmycards.web.components.dialog.core :as dialog]
            [ohmycards.web.controllers.action-dispatcher.core :as sut]
            [ohmycards.web.kws.components.action-dispatcher.core
             :as
             kws.action-dispatcher]
            [ohmycards.web.kws.components.dialog.core :as kws.dialog]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.test-utils :as tu]))

(deftest test-show*

  (letfn [(gen-props [] {::sut/dialog-state (atom nil)
                         ::sut/action-dispatcher-state (atom nil)
                         ::sut/current-hydra-head-atom (atom nil)})]

    (testing "Changes dialog to active"
      (let [props (gen-props)]
        (sut/show* props ::hydra-head)
        (is (true? (kws.dialog/active? @(::sut/dialog-state props))))))

    (testing "Resets action-dispatcher state"
      (with-redefs [action-dispatcher/reset-state (fn [s] (reset! s ::foo))]
        (let [props (gen-props)]
          (sut/show* props ::hydra-head)
          (is (= ::foo @(::sut/action-dispatcher-state props))))))

    (testing "Set's the current hydra head atom to the given hydra head"
      (let [props (gen-props)]
        (sut/show* props ::hydra-head)
        (is (= ::hydra-head @(::sut/current-hydra-head-atom props)))))

    (testing "Don't do nothing if there is no hydra-head"
      (let [props (gen-props)]
        (sut/show* props nil)
        (is (nil? @(::sut/dialog-state props)))
        (is (nil? @(::sut/action-dispatcher-state props)))
        (is (nil? @(::sut/current-hydra-head-atom props)))))))


(deftest test-close*

  (testing "Changes dialog to deactive"
    (let [state (atom nil)]
      (sut/close* {::sut/dialog-state state})
      (is (false? (kws.dialog/active? @state))))))

(deftest test-component*

  (let [hydra-head {kws.hydra/shortcut \c}]
    (letfn [(gen-props [dialog-state dispatcher-state]
              {::sut/dialog-state dialog-state
               ::sut/action-dispatcher-state dispatcher-state
               ::sut/current-hydra-head-atom (atom hydra-head)})]

      (testing "Renders dialog with state"
        (let [state (atom {})
              [c props] (sut/component* (gen-props state (atom nil)))]
          (is (= c dialog/main))
          (is (= state (:state props)))))

      (testing "Renders action-dispatcher with props"
        (let [state (atom {})
              comp (sut/component* (gen-props (atom nil) state))
              props (tu/get-props-for action-dispatcher/main comp)]
          (is (= (:state props) state))
          (is (= (kws.action-dispatcher/actions-hydra-head props) hydra-head))
          (is (fn? (kws.action-dispatcher/dispatch-action! props))))))))
