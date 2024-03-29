(ns ohmycards.web.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.app.dialogs.copy-card-link :as dialogs.copy-card-link]
            [ohmycards.web.app.state :as app.state]
            [ohmycards.web.components.current-view.core :as components.current-view]
            [ohmycards.web.controllers.action-dispatcher.core
             :as
             controllers.action-dispatcher]
            [ohmycards.web.controllers.clipboard-dialog.core
             :as
             controllers.clipboard-dialog]
            [ohmycards.web.controllers.file-upload-dialog.core
             :as
             controllers.file-upload-dialog]
            [ohmycards.web.core :as sut]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.lenses.login :as lenses.login]
            [ohmycards.web.kws.lenses.routing :as lenses.routing]
            [ohmycards.web.kws.services.routing.core :as kws.routing]
            [ohmycards.web.kws.services.routing.pages :as routing.pages]
            [ohmycards.web.services.notify :as services.notify]
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
                  ::components.current-view/view-props       {:state app.state/state}
                  ::components.current-view/header-component ::header-component
                  ::components.current-view/loading?         true}]
                [controllers.action-dispatcher/component]
                [controllers.file-upload-dialog/component]
                [controllers.clipboard-dialog/component]
                [dialogs.copy-card-link/dialog]
                [services.notify/toast]]
               (sut/current-view* state ::home-view ::login-view ::header-component))))

      (testing "Defaults view to home-view"
        (let [state*    (dissoc state ::lenses.routing/match)
              [_ props] (find-main
                         (sut/current-view* state* ::home-view ::login-view ::header-component))]
          (is (= ::home-view (::components.current-view/view props)))))

      (testing "Receives loading? from login status (false)"
        (let [state*    (assoc state ::lenses.login/initialized? true)
              [_ props] (find-main
                         (sut/current-view* state* ::home-view ::login-view ::header-component))]
          (is (false? (::components.current-view/loading? props))))))))

(deftest test-contextual-actions-dispatcher-hydra-head!

  (letfn [(gen-state [route-name] (r/atom {lenses.routing/match {:data {kws.routing/name route-name}}}))]

    (testing "Nil for no route"
      (with-redefs [app.state/state (atom nil)]
        (is (nil? (sut/contextual-actions-dispatcher-hydra-head!)))))

    (testing "Nil for unknown route"
      (with-redefs [app.state/state (gen-state ::unknown)]
        (is (nil? (sut/contextual-actions-dispatcher-hydra-head!)))))

    (testing "With edit-card page, returns edit card actions"
      (with-redefs [app.state/state              (gen-state routing.pages/edit-card)
                    edit-card.handlers/hydra-head #(do {::foo 1})]
        (is (= {::foo 1} (sut/contextual-actions-dispatcher-hydra-head!)))))))
