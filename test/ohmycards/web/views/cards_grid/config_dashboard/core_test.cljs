(ns ohmycards.web.views.cards-grid.config-dashboard.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.inputs.simple :as inputs.simple]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.combobox :as inputs.combobox]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.components.inputs.textarea :as inputs.textarea]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.components.inputs.combobox.core :as kws.combobox]
            [ohmycards.web.kws.components.inputs.combobox.options
             :as
             kws.combobox.options]
            [ohmycards.web.kws.components.inputs.tags :as kws.tags]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.cards-grid.config-dashboard.core :as sut]))

(deftest test-main

  (let [props {::foo 1}
        comp  (sut/main props)]

    (testing "Contains header"
      (is (tu/exists-in-component? [sut/header props] comp)))

    (testing "Contains grid-config"
      (is (tu/exists-in-component? [sut/grid-config props] comp)))
    
    (testing "Contains profile-manager"
      (is (tu/exists-in-component? [sut/profile-manager props] comp)))))

(deftest test-profile-manager

  (let [state (atom {})
        props {:state state}
        comp  (sut/profile-manager props)]

    (testing "Renders the label"
      (is (tu/exists-in-component? [sut/title "Profile Manager"] comp)))

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
          [row row-props] comp
          input (:input row-props)]

      (testing "Renders label"
        (is (= "Load Profile" (:label row-props))))

      (testing "Renders input"
        (is
         (tu/exists-in-component?
          [inputs.combobox/main
           (-> (sut/input-props state [kws/load-profile-name] (coercers/is-in ["Foo"]))
               (assoc kws.combobox/options [{kws.combobox.options/value "Foo"}]))]
          (tu/comp-seq input))))

      (testing "Renders set-btn"
        (let [btn-props (tu/get-props-for sut/set-btn (tu/comp-seq input))]
          (is (fn? (:set-fn btn-props)))
          (is (= state (:state btn-props)))
          (is (= "Load!" (:label btn-props)))
          (is (= [kws/load-profile-name] (:path btn-props))))))))

(deftest test-grid-config

  (let [props {::foo ::bar}
        comp  (sut/grid-config props)
        exists-in-comp? #(tu/exists-in-component? % (tu/comp-seq comp))]

    (testing "Renders title"
      (is (exists-in-comp? [sut/title "General Configuration"])))

    (testing "Renders page-config"
      (is (exists-in-comp? [sut/page-config props])))

    (testing "Renders page-size-config"
      (is (exists-in-comp? [sut/page-size-config props])))

    (testing "Renders include-tags-config"
      (is (exists-in-comp? [sut/include-tags-config props])))

    (testing "Renders exclude-tags-config"
      (is (exists-in-comp? [sut/exclude-tags-config props])))

    (testing "Renders tags-filter-query-config"
      (is (exists-in-comp? [sut/tags-filter-query-config props])))))

(deftest test-save-profile-name

  (with-redefs [sut/input-props vector]

    (testing "With valid profile" 
      (let [state (atom {kws/save-profile-name (coercion.result/success "" "")
                         kws/config {}})
            props {:state state}
            comp  (sut/save-profile-name props)
            [row row-props] comp
            input (:input row-props)]

        (testing "Renders form row"
          (is (= form/row row)))

        (testing "Passes label"
          (is (= "Save Profile" (:label row-props))))

        (testing "Renders input"
          (is
           (tu/exists-in-component?
            [inputs.simple/main
             (sut/input-props state [kws/save-profile-name] sut/string-with-min-len-2)]
            (tu/comp-seq input))))

        (testing "Renders set-btn"
          (let [props (tu/get-props-for sut/set-btn (tu/comp-seq input))]
            (is (= "Save!" (:label props)))
            (is (= state (:state props)))
            (is (= [kws/save-profile-name] (:path props)))
            (is (fn? (:set-fn props)))))))
    
    (testing "With invalid profile" 
      (let [state (atom {kws/config {kws.config/page (coercion.result/failure "" "")}})
            props {:state state}
            comp  (sut/save-profile-name props)
            [row row-props] comp
            input (:input row-props)]

        (testing "Renders error"
          (tu/exists-in-component?
           [error-message-box/main {:value "Invalid values prevent save!"}]
           (tu/comp-seq input)))))))

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
            [_ input-props] (tu/get-first #(= (tu/safe-first %) inputs.simple/main)
                                          (tu/comp-seq (sut/page-config props)))]
        (is (= (:value input-props) "2"))))))

(deftest test-page-site-config

  (let [coerced-value (coercion.result/success "20" 20)]

    (testing "Contains input with value"
      (let [props {:state (atom {kws/config {kws.config/page-size coerced-value}})}
            [_ input-props] (tu/get-first #(= (tu/safe-first %) inputs.simple/main)
                                          (tu/comp-seq (sut/page-size-config props)))]
        (is (= (:value input-props) "20"))))))

(deftest test-include-tags-config

  (let [coerced-value (coercion.result/success ["A"] ["A"])
        props {:state (atom {kws/config {kws.config/include-tags coerced-value}})
               kws/cards-metadata {kws.card-metadata/tags ["A"]}}
        comp (sut/include-tags-config props)
        [row row-props] comp
        input (:input row-props)]

    (testing "Includes label"
      (is (= "ALL tags" (:label row-props))))

    (testing "Contains input with value"
      (let [input-props (tu/get-props-for inputs.tags/main (tu/comp-seq input))]
        (is (= ["A"] (:value input-props)))))

    (testing "Contains input with all tags"
      (let [input-props (tu/get-props-for inputs.tags/main (tu/comp-seq input))]
        (is (= (kws.tags/all-tags input-props) ["A"]))))))

(deftest test-exclude-tags-config

  (let [coerced-value (coercion.result/success ["A"] ["A"])
        cards-metadata {kws.card-metadata/tags ["B"]}
        props {:state (atom {kws/config {kws.config/exclude-tags coerced-value}})
               kws/cards-metadata cards-metadata}
        comp (sut/exclude-tags-config props)
        [row row-props] comp
        input (:input row-props)
        tags-input-props (tu/get-props-for inputs.tags/main (tu/comp-seq input))]

    (testing "Includes label"
      (is (= "NONE OF tags" (:label row-props))))

    (testing "Contains input with value"
      (is (= (:value tags-input-props) ["A"])))

    (testing "Contains input with all tags"
      (is (= (kws.tags/all-tags tags-input-props) ["B"])))))

(deftest test-tags-filter-query-config

  (let [value (coercion.result/raw-value->success "foo")
        path [kws/config kws.config/tags-filter-query]
        props {:state (atom (assoc-in {} path value))}
        comp  (sut/tags-filter-query-config props)
        [row row-props] comp]

    (testing "Includes label"
      (is (= "Tag Filter Query" (:label row-props))))

    (testing "Renders textarea"
      (let [textarea-props (tu/get-props-for inputs.textarea/main (tu/comp-seq comp))]
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
