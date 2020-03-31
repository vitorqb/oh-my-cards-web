(ns ohmycards.web.views.cards-grid.config-dashboard.state-management-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.views.cards-grid.config :as kws.config]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [ohmycards.web.views.cards-grid.config-dashboard.state-management
             :as
             sut]))

(deftest test-reset-config
  (let [new-config {kws.config/page 1
                    kws.config/page-size 2
                    kws.config/include-tags ["A"]
                    kws.config/exclude-tags ["B"]}]
    (is (= {kws/config {kws.config/page (coercion.result/success "1" 1)
                        kws.config/page-size (coercion.result/success "2" 2)
                        kws.config/include-tags (coercion.result/raw-value->success ["A"])
                        kws.config/exclude-tags (coercion.result/raw-value->success ["B"])}}
           (sut/reset-config {} new-config)))))
