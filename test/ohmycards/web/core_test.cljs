(ns ohmycards.web.core-test
  (:require [ohmycards.web.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.components.current-view.core :as components.current-view]
            [ohmycards.web.kws.lenses.login :as lenses.login]
            [ohmycards.web.kws.lenses.routing :as lenses.routing]
            [ohmycards.web.common.focused-atom :as focused-atom]))

(deftest test-current-view*

  (let [state {::lenses.login/current-user ::current-user
               ::lenses.routing/match      {:data {:view ::current-view}}}]

    (testing "Base"
      (is (= [components.current-view/main
              {::components.current-view/current-user     ::current-user
               ::components.current-view/login-view       ::login-view
               ::components.current-view/view             ::current-view
               ::components.current-view/header-component ::header-component}]
             (sut/current-view* state ::home-view ::login-view ::header-component))))

    (testing "Defaults view to home-view"
      (let [state*    (dissoc state ::lenses.routing/match)
            [_ props] (sut/current-view* state* ::home-view ::login-view ::header-component)]
        (is (= ::home-view (::components.current-view/view props)))))))
