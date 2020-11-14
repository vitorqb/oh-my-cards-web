(ns ohmycards.web.views.profiles.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.cards-grid.metadata.core :as kws.cards-grid.metadata]
            [ohmycards.web.kws.components.inputs.combobox.core
             :as
             kws.inputs.combobox]
            [ohmycards.web.kws.components.inputs.combobox.options
             :as
             kws.inputs.combobox.options]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.views.profiles.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.profiles.core :as sut]
            [reagent.core :as r]))

(deftest test-on-submit

  (let [mk-props (fn [] {:state (r/atom nil) kws/load-profile! #(do) kws/goto-grid! #(do)})]

    (testing "Calls fn to load the profile with profile name"
      (let [load-profile-args (atom [])
            load-profile! #(swap! load-profile-args conj %1)
            state (r/atom {::sut/profile-name "name"})
            props (assoc (mk-props) :state state kws/load-profile! load-profile!)]
        (sut/on-submit props )
        (is (= ["name"] @load-profile-args))))

    (testing "Calls fn to redirect the user to the grid"
      (let [goto-grid-calls (atom 0)
            goto-grid! #(swap! goto-grid-calls inc)
            state (r/atom nil)
            props (assoc (mk-props) :state state kws/goto-grid! goto-grid!)]
        (sut/on-submit props)
        (is (= 1 @goto-grid-calls))))))

(deftest test-main
  (testing "Renders input with current selected profile"
    (let [profile-names ["FOO"]
          combobox-options [{kws.inputs.combobox.options/value "FOO"}]
          state (r/atom {})
          cursor (r/cursor state [::sut/profile-name])
          props {:state state kws/profile-names profile-names}
          comp (sut/main props)]
      (is (tu/exists-in-component?
           [inputs/main {kws.inputs/cursor cursor
                         kws.inputs/itype kws.inputs/t-combobox
                         kws.inputs/props {kws.inputs.combobox/options combobox-options}
                         kws.inputs/auto-focus true}]
           (tu/comp-seq comp))))))
