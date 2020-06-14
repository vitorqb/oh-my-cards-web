(ns ohmycards.web.views.cards-grid.control-filter-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.views.cards-grid.core :as kws.cards-grid]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.cards-grid.control-filter :as sut]
            [ohmycards.web.views.cards-grid.state-management :as state-management]))

(deftest test-main

  (testing "Rendered input..."

    (testing "receives value from search term"
      (let [state       (atom {kws.cards-grid/filter-input-search-term "FOO"})
            component   (sut/main {:state state})
            input-props (tu/get-props-for :input.cards-grid-control-filter__input
                                          (tu/comp-seq component))]
        (is (= "FOO" (:value input-props)))))

    (testing "updates search term from user input"
      (let [state       (atom {})
            component   (sut/main {:state state})
            input-props (tu/get-props-for :input.cards-grid-control-filter__input
                                          (tu/comp-seq component))]
        ((:on-change input-props) #js{:target #js{:value "BAR"}})
        (is (= {kws.cards-grid/filter-input-search-term "BAR"} @state)))))

  (testing "Rendered form..."

    (let [props {:state (atom {})}]
      (with-redefs [state-management/commit-search! #(when (= % props) ::result)]
        (let [component-seq (-> props sut/main tu/comp-seq)]

          (testing "calls state-management/commit-search!"
            (let [form-props (tu/get-props-for :form.cards-grid-control-filter__form component-seq)]
              (is (= ::result ((:on-submit form-props) #js{:preventDefault #(do)})))))

          (testing "has submit button"
            (is (tu/exists-in-component? :button.clear-button.cards-grid-control-filter__submit-btn
                                         component-seq))))))))
