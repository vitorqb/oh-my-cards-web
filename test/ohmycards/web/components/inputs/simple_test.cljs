(ns ohmycards.web.components.inputs.simple-test
  (:require [ohmycards.web.components.inputs.simple :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-gen-input-on-change-handler

  (testing "Passes the value to the outer handler"
    (let [on-change #(do [::change %])
          handler (sut/gen-input-on-change-handler on-change)]
      (is (= [::change "foo"] (handler (clj->js {:target {:value "foo"}}))))))

  (testing "Defaults class"
    (let [[_ props] (sut/main {})]
      (is (= "simple-input" (:class props)))))

  (testing "Keeps class if passed"
    (let [[_ props] (sut/main {:class "FOO"})]
      (is (= "FOO" (:class props))))))

(deftest test-build-props

  (let [gen-state #(atom {:foo {:bar "VAL"}})
        path      [:foo :bar]]

    (testing "Sets value"
      (is (= "VAL"
             (:value (sut/build-props (gen-state) path)))))

    (testing "Set's on change function"
      (let [state     (gen-state)
            result    (sut/build-props state path)
            on-change (:on-change result)]
        (is (= "FOO" (get-in (on-change "FOO") path)))
        (is (= "FOO" (get-in @state path)))))

    (testing "Set's on change function using parse-fn"
      (let [state     (gen-state)
            result    (sut/build-props state path :parse-fn #(do [::foo %1]))
            on-change (:on-change result)]
        (is (= [::foo "FOO"] (get-in (on-change "FOO") path)))
        (is (= [::foo "FOO"] (get-in @state path)))))

    (testing "Set's extra kargs"
      (is (= "FOO" (:foo (sut/build-props (gen-state) path :foo "FOO")))))

    (testing "Don't set on-change if disabled"
      (is (nil? (:on-change (sut/build-props (gen-state) path :disabled true)))))))

(deftest test-input

  (testing "Wraps on-change with gen-input-on-change-handler"
    (with-redefs [sut/gen-input-on-change-handler #(do [::gen-handler %])]
      (let [[_ props] (sut/main {:on-change ::on-change})]
        (is (= (:on-change props) [::gen-handler ::on-change]))))))
