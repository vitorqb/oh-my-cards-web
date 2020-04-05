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
                         ::sut/action-dispatcher-state (atom nil)})]

    (testing "Changes dialog to active"
      (let [props (gen-props)]
        (sut/show* props)
        (is (true? (kws.dialog/active? @(::sut/dialog-state props))))))

    (testing "Resets action-dispatcher state"
      (with-redefs [action-dispatcher/reset-state (fn [s] (reset! s ::foo))]
        (let [props (gen-props)]
          (sut/show* props)
          (is (= ::foo @(::sut/action-dispatcher-state props))))))))


(deftest test-close*

  (testing "Changes dialog to deactive"
    (let [state (atom nil)]
      (sut/close* {::sut/dialog-state state})
      (is (false? (kws.dialog/active? @state))))))

(deftest test-component*

  (testing "Renders dialog with state"
    (let [state (atom {})
          [c props] (sut/component* {::sut/dialog-state state})]
      (is (= c dialog/main))
      (is (= state (:state props)))))

  (testing "Renders action-dispatcher with props"
    (let [state (atom {})
          hydra-head {kws.hydra/shortcut \c}
          comp (sut/component* {::sut/action-dispatcher-state state
                                ::sut/actions-dispatcher-hydra-options hydra-head})
          props (tu/get-props-for action-dispatcher/main comp)]
      (is (= (:state props) state))
      (is (= (kws.action-dispatcher/actions-hydra-head props) hydra-head))
      (is (fn? (kws.action-dispatcher/dispatch-action! props) hydra-head)))))
