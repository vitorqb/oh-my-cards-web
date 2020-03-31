(ns ohmycards.web.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.current-view.core :as components.current-view]
            [ohmycards.web.core :as sut]
            [ohmycards.web.kws.lenses.login :as lenses.login]
            [ohmycards.web.kws.lenses.routing :as lenses.routing]
            [ohmycards.web.test-utils :as tu]))

(deftest test-current-view*

  (let [state {::lenses.login/current-user ::current-user
               ::lenses.routing/match      {:data {:view ::current-view}}}]


    (letfn [(find-main [c] (tu/get-first #(= (tu/safe-first %) components.current-view/main) c))]

      (testing "Base"
        (is (= [:<>
                [components.current-view/main
                 {::components.current-view/current-user     ::current-user
                  ::components.current-view/login-view       ::login-view
                  ::components.current-view/view             ::current-view
                  ::components.current-view/header-component ::header-component}]
                [sut/action-dispatcher-dialog]]
               (sut/current-view* state ::home-view ::login-view ::header-component))))

      (testing "Defaults view to home-view"
        (let [state*    (dissoc state ::lenses.routing/match)
              [_ props] (find-main
                         (sut/current-view* state* ::home-view ::login-view ::header-component))]
          (is (= ::home-view (::components.current-view/view props))))))))
