(ns ohmycards.web.components.inputs.combobox-test
  (:require [ohmycards.web.components.inputs.combobox :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.components.inputs.combobox.core :as kws]
            [ohmycards.web.kws.components.inputs.combobox.options :as kws.options]
            [ohmycards.web.test-utils :as tu]))

(deftest test-filter-options-by-value

  (testing "Base"
    (is
     (=
      [{kws.options/value "foo and bar"}]
      (sut/filter-options-by-value
       [{kws.options/value "foo and bar"} {kws.options/value "foo"} {kws.options/value "bar"}]
       "foo bar"))))

  (testing "Nil"
    (is
     (=
      [{kws.options/value "a"} {kws.options/value "b"}]
      (sut/filter-options-by-value
       [{kws.options/value "a"} {kws.options/value "b"}]
       nil)))))

(deftest test-main

  (testing "Renders one ComboboxOption for each kws/options"
    (let [props {kws/options [{kws.options/value "Foo"} {kws.options/value "Bar"}]}
          comp  (sut/main props)]
      (is (tu/exists-in-component? [sut/ComboboxOption {:value "Foo"}] comp))
      (is (tu/exists-in-component? [sut/ComboboxOption {:value "Bar"}] comp)))))
