(ns ohmycards.web.views.cards-grid.config-dashboard.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.combobox :as inputs.combobox]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.components.inputs.textarea :as inputs.textarea]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.components.inputs.combobox.core :as kws.combobox]
            [ohmycards.web.kws.components.inputs.combobox.options
             :as
             kws.combobox.options]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.components.inputs.tags :as kws.inputs.tags]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.cards-grid.config-dashboard.core :as sut]
            [reagent.core :as r]))

(deftest test-main

  (let [props {::foo 1}
        comp  (sut/main props)]

    (testing "Contains header"
      (is (tu/exists-in-component? [sut/header props] comp)))

    (testing "Contains grid-config"
      (is (tu/exists-in-component? [sut/grid-config props] comp)))
    
    (testing "Contains profile-manager"
      (is (tu/exists-in-component? [sut/profile-manager props] comp)))))

(deftest test-set-btn

  (letfn [(find-btn [comp] (tu/get-first
                            #(= (tu/safe-first %) :button.cards-grid-config-dashboard__set)
                            comp))]

    (testing "Renders the text Set inside a button"
      (let [cursor (r/cursor (r/atom {}) [:foo])
            comp (sut/set-btn {:cursor cursor :set-fn #(do)})
            [_ _ label] (find-btn comp)]
        (is (= label "Set"))))

    (testing "Passe set-fn to button"
      (let [cursor (r/cursor (r/atom {}) [:foo])
            set-fn #(reset! cursor 1)
            [_ props _] (find-btn (sut/set-btn {:cursor cursor :set-fn set-fn}))]
        ((:on-click props))
        (is (= 1 @cursor))))

    (testing "If has coercion error..."
      (let [cursor (r/atom (coercion.result/failure "" "err"))
            comp  (sut/set-btn {:cursor cursor :set-fn #(do)})]

        (testing "does not render button"
          (is (nil? (find-btn comp))))

        (testing "renders an error message box with correct value"
          (let [[c props] comp]
            (is (= c error-message-box/main))
            (is (= "err" (:value props)))))))))

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

  (with-redefs [coercers/is-in  (fn [& xs] [::is-n xs])]

    (let [state (r/atom {})
          cursor (r/cursor state [kws/load-profile-name])
          props {:state state kws/profiles-names ["Foo"]}
          comp  (sut/load-profile-name props)
          row-props (tu/get-props-for form/row (tu/comp-seq comp))]

      (testing "Renders label"
        (is (= "Load Profile" (:label row-props))))

      (testing "Renders input"
        (is
         (tu/exists-in-component?
          [inputs/main
           {kws.inputs/itype kws.inputs/t-combobox
            kws.inputs/props {kws.combobox/options [{kws.combobox.options/value "Foo"}]}
            kws.inputs/cursor cursor
            kws.inputs/coercer [::is-n [["Foo"]]]}]
          (tu/comp-seq comp))))

      (testing "Renders set-btn"
        (let [btn-props (tu/get-props-for sut/set-btn (tu/comp-seq comp))]
          (is (fn? (:set-fn btn-props)))
          (is (= cursor (:cursor btn-props)))
          (is (= "Load!" (:label btn-props))))))))

(deftest test-save-profile-name

  (testing "With valid profile" 
    (let [state (r/atom {kws/save-profile-name (coercion.result/success "" "")
                         kws/config {}})
          cursor (r/cursor state [kws/save-profile-name])
          props {:state state}
          comp  (sut/save-profile-name props)
          row-props (tu/get-props-for form/row (tu/comp-seq comp))]

      (testing "Passes label"
        (is (= "Save Profile" (:label row-props))))

      (testing "Renders input"
        (is
         (tu/exists-in-component?
          [inputs/main
           {kws.inputs/coercer sut/string-with-min-len-2
            kws.inputs/cursor cursor}]
          (tu/comp-seq comp))))

      (testing "Renders set-btn"
        (let [props (tu/get-props-for sut/set-btn (tu/comp-seq comp))]
          (is (= "Save!" (:label props)))
          (is (= cursor (:cursor props)))
          (is (fn? (:set-fn props)))))))
  
  (testing "With invalid profile" 
    (let [state (r/atom {kws/config {kws.config/page (coercion.result/failure "" "")}})
          props {:state state}
          comp  (sut/save-profile-name props)]

      (testing "Renders error"
        (tu/exists-in-component?
         [error-message-box/main {:value "Invalid values prevent save!"}]
         (tu/comp-seq (tu/comp-seq comp)))))))

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

  (testing "Contains input with cursor"
    (let [state (atom {})]
      (with-redefs [r/cursor #(do [::cursor %1 %2])]
        (is (tu/exists-in-component?
             [inputs/main
              {kws.inputs/cursor [::cursor state [kws/config kws.config/page]]
               kws.inputs/coercer sut/positive-int-or-nil-coercer
               kws.inputs/props {:class "simple-input simple-input--small"}}]
             (tu/comp-seq (sut/page-config {:state state})))))))

  (testing "set-fn calls set-page!"
    (let [state (r/atom {})
          calls (atom 0)
          props {:state state kws/set-page! #(swap! calls inc)}
          comp (sut/page-config props)
          set-btn-props (tu/get-props-for sut/set-btn (tu/comp-seq comp))]
      ((:set-fn set-btn-props))
      (is (= 1 @calls)))))

(deftest test-page-site-config

  (testing "Contains input with cursor"
    (let [state (atom {})]
      (with-redefs [r/cursor #(do [::cursor %1 %2])]
        (is (tu/exists-in-component?
             [inputs/main
              {kws.inputs/cursor [::cursor state [kws/config kws.config/page-size]]
               kws.inputs/coercer sut/positive-int-or-nil-coercer
               kws.inputs/props {:class "simple-input simple-input--small"}}]
             (tu/comp-seq (sut/page-size-config {:state state})))))))

  (testing "set-fn calls set-page-size!"
    (let [state (r/atom {})
          calls (atom 0)
          props {:state state kws/set-page-size! #(swap! calls inc)}
          comp (sut/page-size-config props)
          set-btn-props (tu/get-props-for sut/set-btn (tu/comp-seq comp))]
      ((:set-fn set-btn-props))
      (is (= 1 @calls)))))

(deftest test-include-tags-config

  (testing "Contains input with cursor"
    (let [state (atom {})
          props {:state state kws/cards-metadata {kws.card-metadata/tags ["A"]}}]
      (with-redefs [r/cursor #(do [::cursor %1 %2])]
        (is (tu/exists-in-component?
             [inputs/main
              {kws.inputs/cursor [::cursor state [kws/config kws.config/include-tags]]
               kws.inputs/coercer coercers/tags
               kws.inputs/itype kws.inputs/t-tags
               kws.inputs/props {kws.inputs.tags/all-tags ["A"]}}]
             (tu/comp-seq (sut/include-tags-config props)))))))

  (testing "set-fn calls set-include-tags!"
    (let [state (r/atom {})
          calls (atom 0)
          props {:state state kws/set-include-tags! #(swap! calls inc)}
          comp (sut/include-tags-config props)
          set-btn-props (tu/get-props-for sut/set-btn (tu/comp-seq comp))]
      ((:set-fn set-btn-props))
      (is (= 1 @calls)))))

(deftest test-exclude-tags-config

  (testing "Contains input with cursor"
    (let [state (atom {})
          props {:state state kws/cards-metadata {kws.card-metadata/tags ["A"]}}]
      (with-redefs [r/cursor #(do [::cursor %1 %2])]
        (is (tu/exists-in-component?
             [inputs/main
              {kws.inputs/cursor [::cursor state [kws/config kws.config/exclude-tags]]
               kws.inputs/coercer coercers/tags
               kws.inputs/itype kws.inputs/t-tags
               kws.inputs/props {kws.inputs.tags/all-tags ["A"]}}]
             (tu/comp-seq (sut/exclude-tags-config props)))))))

  (testing "set-fn calls set-exclude-tags!"
    (let [state (r/atom {})
          calls (atom 0)
          props {:state state kws/set-exclude-tags! #(swap! calls inc)}
          comp (sut/exclude-tags-config props)
          set-btn-props (tu/get-props-for sut/set-btn (tu/comp-seq comp))]
      ((:set-fn set-btn-props))
      (is (= 1 @calls)))))

(deftest test-tags-filter-query-config

  (testing "Contains input with cursor"
    (let [state (atom {})]
      (with-redefs [r/cursor #(do [::cursor %1 %2])]
        (is (tu/exists-in-component?
             [inputs/main
              {kws.inputs/itype kws.inputs/t-textarea
               kws.inputs/cursor [::cursor state [kws/config kws.config/tags-filter-query]]
               kws.inputs/coercer coercers/string}]
             (tu/comp-seq (sut/tags-filter-query-config {:state state})))))))

  (testing "set-fn calls set-tags-filter-query"
    (let [state (r/atom {})
          calls (atom 0)
          props {:state state kws/set-tags-filter-query! #(swap! calls inc)}
          comp (sut/tags-filter-query-config props)
          set-btn-props (tu/get-props-for sut/set-btn (tu/comp-seq comp))]
      ((:set-fn set-btn-props))
      (is (= 1 @calls)))))
