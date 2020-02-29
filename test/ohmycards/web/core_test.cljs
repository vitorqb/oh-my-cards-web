(ns ohmycards.web.core-test
  (:require [ohmycards.web.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.components.current-view.core :as components.current-view]
            [ohmycards.web.kws.lenses.login :as lenses.login]
            [ohmycards.web.kws.lenses.routing :as lenses.routing]))

(deftest test-current-view*

  (let [state {::lenses.login/current-user ::current-user
               ::lenses.routing/match      {:view ::current-view}}]

    (testing "Base"
      (is (= [components.current-view/main
              {::components.current-view/current-user ::current-user
               ::components.current-view/login-view   ::login-view
               ::components.current-view/view         ::current-view}]
             (sut/current-view* state ::home-view ::login-view))))

    (testing "Defaults view to home-view"
      (let [state*    (dissoc state ::lenses.routing/match)
            [_ props] (sut/current-view* state* ::home-view ::login-view)]
        (is (= ::home-view (::components.current-view/view props)))))))
