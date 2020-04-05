(ns ohmycards.web.views.cards-grid.config-dashboard.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.inputs.combobox :as inputs.combobox]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.kws.components.inputs.combobox.core :as kws.combobox]
            [ohmycards.web.kws.components.inputs.combobox.options
             :as
             kws.combobox.options]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.cards-grid.config-dashboard.core :as sut]))

(deftest test-main

  (let [props {::foo 1}
        comp  (sut/main props)]

    (testing "Contains header"
      (is (tu/exists-in-component? [sut/header props] comp)))

    (testing "Contains include-tags config"
      (is (tu/exists-in-component? [sut/include-tags-config props] comp)))

    (testing "Contains profile-manager"
      (is (tu/exists-in-component? [sut/profile-manager props] comp)))))

(deftest test-profile-manager

  (let [state (atom {})
        props {:state state}
        comp  (sut/profile-manager props)]

    (testing "Renders the label"
      (is (tu/exists-in-component? (sut/label "Profile Manager") comp)))

    (testing "Renders the load-profile-name"
      (is (tu/exists-in-component? [sut/load-profile-name props] comp)))))

(deftest test-load-profile-name

  (with-redefs [sut/input-props (fn [a b c & xs] {:a a :b b :c c :xs xs})
                coercers/is-in  (fn [& xs] [::is-n xs])]

    (let [state (atom {})
          props {:state state kws/profiles-names ["Foo"]}
          comp  (sut/load-profile-name props)
          exists-in-comp? #(tu/exists-in-component? % comp)
          props-for #(tu/get-props-for % comp)]

      (testing "Renders label"
        (is (exists-in-comp? (sut/label "Load Profile"))))

      (testing "Renders input-wrapper"
        (is
         (exists-in-comp?
          [sut/input-wrapper {}
           [inputs.combobox/main
            (sut/input-props state [kws/load-profile-name] (coercers/is-in ["Foo"])
                             kws.combobox/options [{kws.combobox.options/value "Foo"}])]])))

      (testing "Renders set-btn"
        (let [btn-props (props-for sut/set-btn)]
          (is (fn? (:set-fn btn-props)))
          (is (= state (:state btn-props)))
          (is (= [kws/load-profile-name] (:path btn-props))))))))

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
