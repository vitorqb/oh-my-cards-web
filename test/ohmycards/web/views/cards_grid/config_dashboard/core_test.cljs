(ns ohmycards.web.views.cards-grid.config-dashboard.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.kws.views.cards-grid.config :as kws.config]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.cards-grid.config-dashboard.core :as sut]))

(deftest test-main

  (testing "Contains header"
    (let [props {::foo 1}]
      (is (tu/exists-in-component? [sut/header props] (sut/main props)))))

  (testing "Contains include-tags config"
    (let [props {::foo 1}]
      (is (tu/exists-in-component? [sut/include-tags-config props] (sut/main props))))))

(deftest test-page-config

  (let [coerced-value (coercion.result/success "2" 2)]

    (testing "Contains input with value"
      (let [props {:state (atom {kws/config {kws.config/page coerced-value}})}
            [_ input-props] (tu/get-first #(= (tu/safe-first %) form.input/main)
                                          (tu/comp-seq (sut/page-config props)))]
        (is (= (:value input-props) "2"))))))

(deftest test-page-site-config

  (let [coerced-value (coercion.result/success "20" 20)]

    (testing "Contains input with value"
      (let [props {:state (atom {kws/config {kws.config/page-size coerced-value}})}
            [_ input-props] (tu/get-first #(= (tu/safe-first %) form.input/main)
                                          (tu/comp-seq (sut/page-size-config props)))]
        (is (= (:value input-props) "20"))))))

(deftest test-include-tags-config

  (let [coerced-value (coercion.result/success ["A"] ["A"])
        props {:state (atom {kws/config {kws.config/include-tags coerced-value}})}]

    (testing "Includes label"
      (is (tu/exists-in-component? (sut/label "ALL tags") (sut/include-tags-config props))))

    (testing "Contains input with value"
      (let [comp (sut/include-tags-config props)
            [_ input-props] (tu/get-first #(= (tu/safe-first %) inputs.tags/main)
                                          (tu/comp-seq comp))]
        (is (= (:value input-props) ["A"]))))))

(deftest test-exclude-tags-config

  (let [coerced-value (coercion.result/success ["A"] ["A"])]

    (testing "Includes label"
      (let [props {:state (atom {kws/config {kws.config/exclude-tags coerced-value}})}]
        (is (tu/exists-in-component? (sut/label "Not ANY tags") (sut/exclude-tags-config props)))))

    (testing "Contains input with value"
      (let [props {:state (atom {kws/config {kws.config/exclude-tags coerced-value}})}
            comp (sut/exclude-tags-config props)
            [_ input-props] (tu/get-first #(= (tu/safe-first %) inputs.tags/main)
                                          (tu/comp-seq comp))]
        (is (= (:value input-props) ["A"]))))))

(deftest test-set-btn

  (letfn [(find-btn [comp] (tu/get-first
                            #(= (tu/safe-first %) :button.cards-grid-config-dashboard__set)
                            comp))]

    (testing "Renders the text Set inside a button"
      (let [comp        (sut/set-btn {:state (atom {}) :path [:foo] :set-fn #(do)})
            [_ _ label] (find-btn comp)]
        (is (= label "Set"))))

    (testing "Passe set-fn to button"
      (let [state  (atom {})
            set-fn #(swap! state assoc :foo 1)
            [_ props _] (find-btn (sut/set-btn {:state state :path [:foo] :set-fn set-fn}))]
        ((:on-click props))
        (is (= 1 (:foo @state)))))

    (testing "If has coercion error..."
      (let [state (atom {:foo (coercion.result/failure "" "err")})
            comp  (sut/set-btn {:state state :path [:foo] :set-fn #(do)})]

        (testing "does not render button"
          (is (nil? (find-btn comp))))

        (testing "renders an error message box with correct value"
          (let [[c props] comp]
            (is (= c error-message-box/main))
            (is (= "err" (:value props)))))))))
