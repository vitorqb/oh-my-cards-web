(ns ohmycards.web.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.current-view.core :as components.current-view]
            [ohmycards.web.controllers.action-dispatcher.core
             :as
             controllers.action-dispatcher]
            [ohmycards.web.core :as sut]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.lenses.login :as lenses.login]
            [ohmycards.web.kws.lenses.routing :as lenses.routing]
            [ohmycards.web.kws.services.routing.core :as kws.routing]
            [ohmycards.web.kws.services.routing.pages :as routing.pages]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.edit-card.handlers :as edit-card.handlers]
            [reagent.core :as r]))

(deftest test-current-view*

  (let [state {::lenses.login/current-user ::current-user
               ::lenses.routing/match      {:data {kws.routing/view ::current-view}}}]


    (letfn [(find-main [c] (tu/get-first #(= (tu/safe-first %) components.current-view/main) c))]

      (testing "Base"
        (is (= [:<>
                [components.current-view/main
                 {::components.current-view/current-user     ::current-user
                  ::components.current-view/login-view       ::login-view
                  ::components.current-view/view             ::current-view
                  ::components.current-view/header-component ::header-component}]
                [controllers.action-dispatcher/component]]
               (sut/current-view* state ::home-view ::login-view ::header-component))))

      (testing "Defaults view to home-view"
        (let [state*    (dissoc state ::lenses.routing/match)
              [_ props] (find-main
                         (sut/current-view* state* ::home-view ::login-view ::header-component))]
          (is (= ::home-view (::components.current-view/view props))))))))

(deftest test-contextual-actions-dispatcher-hydra-head!

  (letfn [(gen-state [route-name] (r/atom {lenses.routing/match {:data {kws.routing/name route-name}}}))]

    (testing "Nil for no route"
      (with-redefs [sut/state (atom nil)]
        (is (nil? (sut/contextual-actions-dispatcher-hydra-head!)))))

    (testing "Nil for unknown route"
      (with-redefs [sut/state (gen-state ::unknown)]
        (is (nil? (sut/contextual-actions-dispatcher-hydra-head!)))))

    (testing "With edit-card page, returns edit card actions"
      (with-redefs [sut/state                     (gen-state routing.pages/edit-card)
                    edit-card.handlers/hydra-head #(do {::foo 1})]
        (is (= {::foo 1} (sut/contextual-actions-dispatcher-hydra-head!)))))))
