(ns ohmycards.web.components.copy-card-link-dialog.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.copy-card-link-dialog.core :as sut]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.components.copy-card-link-dialog.core :as kws]
            [ohmycards.web.kws.components.inputs.core :as inputs.kws]
            [ohmycards.web.test-utils :as tu]
            [reagent.core :as r]))

(defn- mk-props
  [{::kws/keys [value to-clipboard!]
    :or {value ""
         to-clipboard! (constantly nil)}}]
  {:state (r/atom {kws/value value})
   kws/to-clipboard! to-clipboard!})

(defn- mk-component [opts]
  (sut/main (mk-props opts)))

(deftest test-handle-submit!

  (testing "Copies to clipboard"
    (let [to-clipboard! (tu/new-stub)
          props (mk-props {kws/to-clipboard! to-clipboard!
                           kws/value "1234"})]
      (with-redefs [sut/hide! (tu/new-stub)]
        (sut/handle-submit! props)
        (is (= [["/#/cards/display?ref=1234"]] (tu/get-calls to-clipboard!))))))

  (testing "Closes the dialog"
    (let [hide! (tu/new-stub)
          props (mk-props {kws/value "1234"})]
      (with-redefs [sut/hide! hide!]
        (sut/handle-submit! props)
        (is (= [[props]] (tu/get-calls hide!)))))))

(deftest test-main

  (testing "When form is submitted, call `handle-submit!`"
    (let [handle-submit! (tu/new-stub)]
      (with-redefs [sut/handle-submit! handle-submit!]
        (let [props (mk-props {})
              component (sut/main props)
              form-props (tu/get-props-for form/main (tu/comp-seq component))]
          ((::form/on-submit form-props))
          (is (= [[props]] (tu/get-calls handle-submit!)))))))

  (testing "Renders an input with `value`"
    (let [component (mk-component {kws/value "FOO"})
          input-props (tu/get-props-for inputs/main (tu/comp-seq component))]
      (is (= true (inputs.kws/auto-focus input-props)))
      (is (= "FOO" @(inputs.kws/cursor input-props))))))
