(ns ohmycards.web.views.cards-grid.config-dashboard.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.inputs.combobox :as inputs.combobox]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.components.inputs.textarea :as inputs.textarea]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.components.inputs.combobox.core :as kws.combobox]
            [ohmycards.web.kws.components.inputs.combobox.options
             :as
             kws.combobox.options]
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

    (testing "Contains tags filter query config"
      (is (tu/exists-in-component? [sut/tags-filter-query-config props] comp)))

    (testing "Contains profile-manager"
      (is (tu/exists-in-component? [sut/profile-manager props] comp)))))

(deftest test-profile-manager

  (let [state (atom {})
        props {:state state}
        comp  (sut/profile-manager props)]

    (testing "Renders the label"
      (is (tu/exists-in-component? (sut/label "Profile Manager") comp)))

    (testing "Renders the load-profile-name"
      (is (tu/exists-in-component? [sut/load-profile-name props] comp)))

    (testing "Renders the save-profile-name"
      (is (tu/exists-in-component? [sut/save-profile-name props] comp)))))

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
          (is (= "Load!" (:label btn-props)))
          (is (= [kws/load-profile-name] (:path btn-props))))))))

(deftest test-save-profile-name

  (with-redefs [sut/input-props vector]

    (testing "With valid profile" 
      (let [state (atom {kws/save-profile-name (coercion.result/success "" "")
                         kws/config {}})
            props {:state state}
            comp  (sut/save-profile-name props)
            exists-in-comp? #(tu/exists-in-component? % comp)
            props-for #(tu/get-props-for % comp)]

        (testing "Renders label"
          (is (exists-in-comp? (sut/label "Save Profile"))))

        (testing "Renders input-wrapper"
          (is
           (exists-in-comp?
            [sut/input-wrapper {}
             [form.input/main
              (sut/input-props state [kws/save-profile-name] sut/string-with-min-len-2)]])))

        (testing "Renders set-btn"
          (let [props (props-for sut/set-btn)]
            (is (= "Save!" (:label props)))
            (is (= state (:state props)))
            (is (= [kws/save-profile-name] (:path props)))
            (is (fn? (:set-fn props)))))))
    
    (testing "With invalid profile" 
      (let [state (atom {kws/config {kws.config/page (coercion.result/failure "" "")}})
            props {:state state}
            comp  (sut/save-profile-name props)
            exists-in-comp? #(tu/exists-in-component? % comp)
            props-for #(tu/get-props-for % comp)]

        (testing "Renders error"
          (exists-in-comp? [error-message-box/main {:value "Invalid values prevent save!"}]))))))

(deftest test-get-profile-for-save

  (let [profile-name (coercion.result/raw-value->success "FOO")
        exclude-tags (coercion.result/raw-value->success ["A"])
        include-tags (coercion.result/raw-value->success ["B"])
        page (coercion.result/raw-value->success 1)
        page-size (coercion.result/raw-value->success 2)
        state {kws/save-profile-name profile-name
               kws/config {kws.config/exclude-tags exclude-tags
                           kws.config/include-tags include-tags
                           kws.config/page page
                           kws.config/page-size page-size}}]

    (testing "When all coerced values are fined, return the profile"
      (is (= {kws.profile/name "FOO"
              kws.profile/config {kws.config/exclude-tags ["A"]
                                  kws.config/include-tags ["B"]
                                  kws.config/page 1
                                  kws.config/page-size 2}}
             (sut/get-profile-for-save state))))

    (testing "When the coerced values are not fine, results nil"
      (let [page' (coercion.result/failure "" "")
            state' (assoc-in state [kws/config kws.config/page] page')]
        (is (nil? (sut/get-profile-for-save state')))))))

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

(deftest test-tags-filter-query-config

  (let [value (coercion.result/raw-value->success "foo")
        path [kws/config kws.config/tags-filter-query]
        props {:state (atom (assoc-in {} path value))}
        comp  (sut/tags-filter-query-config props)]

    (testing "Includes label"
      (is (tu/exists-in-component? (sut/label "Tag Filter Query") comp)))

    (testing "Renders textarea"
      (let [textarea-props (tu/get-props-for inputs.textarea/main (tu/comp-seq comp))]
        (cljs.pprint/pprint comp)
        (cljs.pprint/pprint textarea-props)
        (is (ifn? (:on-change textarea-props)))
        (is (= "foo" (:value textarea-props)))))))

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
