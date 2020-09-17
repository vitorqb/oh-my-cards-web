(ns ohmycards.web.controllers.cards-grid.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.controllers.cards-grid.core :as sut]
            [ohmycards.web.kws.services.routing.core :as kws.routing]
            [ohmycards.web.services.routing.core :as routing.core]
            [ohmycards.web.views.cards-grid.config-dashboard.state-management
             :as
             config-dashboard.state-management]
            [reagent.core :as r]))

(deftest test-init-grid-state!
  (binding [sut/*grid-state* nil]
    (let [app-state (r/atom {})]
      (sut/init-grid-state! app-state)
      (is (= sut/*grid-state* (r/cursor app-state [:views.cards-grid]))))))

(deftest test-init-config-dashboard-state!
  (binding [sut/*config-dashboard-state* nil]
    (let [app-state (r/atom {})
          exp-bind  (r/cursor app-state [:views.cards-grid.config-dashboard])
          exp-state (config-dashboard.state-management/init-state {})]
      (sut/init-config-dashboard-state! app-state)
      (is (= exp-bind sut/*config-dashboard-state*))
      (is (= exp-state @sut/*config-dashboard-state*)))))
