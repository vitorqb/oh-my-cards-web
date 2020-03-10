(ns ohmycards.web.views.new-card.form-test
  (:require [ohmycards.web.views.new-card.form :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.kws.card :as kws.card]))

(deftest test-title-input

  (testing "On change mutates state"
    (let [state (atom {})
          comp  (sut/title-input {:state state})
          [_ {:keys [on-change]}] (tu/get-first #(= (tu/safe-first %) form/input) comp)]
      (on-change "foo")
      (is (= @state {kws/card-input {kws.card/title "foo"}}))))

  (testing "Passes value"
    (let [state (atom {kws/card-input {kws.card/title "Foo"}})
          comp  (sut/title-input {:state state})
          [_ {:keys [value]}] (tu/get-first #(= (tu/safe-first %) form/input) comp)]
      (is (= value "Foo")))))

(deftest test-body-input

  (testing "On change mutates state"
    (let [state (atom {})
          comp  (sut/body-input {:state state})
          [_ {:keys [on-change]}] (tu/get-first #(= (tu/safe-first %) form/input) comp)]
      (on-change "foo")
      (is (= @state {kws/card-input {kws.card/body "foo"}}))))

  (testing "Passes value"
    (let [state (atom {kws/card-input {kws.card/body "Foo"}})
          comp  (sut/body-input {:state state})
          [_ {:keys [value]}] (tu/get-first #(= (tu/safe-first %) form/input) comp)]
      (is (= value "Foo")))))

(deftest test-main

  (testing "Renders a title input"
    (let [props {kws/goto-home! (constantly ::foo)}]
      (is
       (some
        #(= [sut/title-input props] %)
        (tu/comp-seq (sut/main props))))))

  (testing "Renders a body input"
    (let [props {kws/goto-home! (constantly ::foo)}]
      (is
       (some
        #(= [sut/body-input props] %)
        (tu/comp-seq (sut/main props)))))))
