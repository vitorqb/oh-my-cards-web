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

(deftest test-load-profile-from-route-match!

  (let [route-match {:query-params {:grid-profile "FOO" :bar "Bar"}
                     :data {kws.routing/name ::name}}]

    (testing "Don't do anything if no profile name on the route"
      (let [route-match (assoc-in route-match [:query-params :grid-profile] nil)
            calls (atom [])]
        (with-redefs [sut/load-profile! #(swap! calls conj [::load-profile %1])
                      routing.core/goto! #(swap! calls conj [::goto %1 %2])]
          (is (nil? (sut/load-profile-from-route-match! route-match)))
          (is (= @calls [])))))

    (testing "When there is a route match..."
      (let [calls (atom [])]
        (with-redefs [sut/load-profile! #(swap! calls conj [::load-profile %1])
                      routing.core/goto! #(swap! calls conj [::goto %1 %&])]
          (let [response (sut/load-profile-from-route-match! route-match)]

            (testing "calls load-profile with the profile name"
              (is (= [::load-profile "FOO"] (@calls 0))))

            (testing "calls routing goto with a new query-params without grid-profile"
              (let [exp-query-params {:bar "Bar"}]
                (is (= [::goto ::name [kws.routing/query-params exp-query-params]]
                       (@calls 1)))))))))))
