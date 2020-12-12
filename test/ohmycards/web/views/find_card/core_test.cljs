(ns ohmycards.web.views.find-card.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.views.find-card.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.find-card.core :as sut]
            [reagent.core :as r]))

(defn- mk-props
  [{::kws/keys [value]
    :or {value ""}}]
  {:state (r/atom {kws/value value})})

(defn- mk-component [opts] (sut/main (mk-props opts)))

(deftest test-main

  (testing "Renders an input with value from state"
    (let [comp  (mk-component {kws/value "123"})
          input-props (tu/get-props-for inputs/main (tu/comp-seq comp))]
      (is (= (-> input-props kws.inputs/cursor deref) "123"))))

  (testing "Renders a form submit btn"
    (let [comp (mk-component {})]
      (is (tu/exists-in-component? [:input {:type "submit"}] (tu/comp-seq comp)))))

  (testing "Handles form submission"
    (let [calls (atom 0)
          comp (mk-component {})
          form-props (tu/get-props-for form/main (tu/comp-seq comp))]
      (with-redefs [sut/handle-submit! #(swap! calls inc)]
        (is (= 0 @calls))
        ((:on-submit form-props))
        (is (= 1 @calls))))))
