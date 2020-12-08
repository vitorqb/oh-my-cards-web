(ns ohmycards.web.components.clipboard-dialog.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.clipboard-dialog.core :as sut]
            [ohmycards.web.components.dialog.core :as components.dialog]
            [ohmycards.web.kws.components.clipboard-dialog.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [reagent.core :as r]))

(defn- mk-props
  ([] (mk-props nil))
  ([{::kws/keys [text to-clipboard!]
     :or {text "FOO"
          to-clipboard! (constantly nil)}}]
   {:state (r/atom {kws/text text})
    kws/to-clipboard! to-clipboard!}))

(deftest test-main

  (testing "Renders textarea with given text as value"
    (let [component (sut/main (mk-props))
          textarea-props (tu/get-props-for :textarea.clipboard-dialog__textarea
                                           (tu/comp-seq component))]
      (is (= "FOO" (:value textarea-props)))))

  (testing "When clicking button, copies to clipboard"
    (let [call (atom nil)]
      (with-redefs [sut/on-copy! #(reset! call %&)]
        (let [props (mk-props)
              component (sut/main props)
              button-props (tu/get-props-for :button (tu/comp-seq component))]
          ((:on-click button-props))
          (is (= @call [props])))))))

(deftest test-show!

  (testing "Set's text to state"
    (let [props (mk-props)]
      (sut/show! props "BAR")
      (is (= "BAR" (kws/text @(:state props))))))

  (testing "Set's dialog to visible"
    (let [props (mk-props)]
      (sut/show! props "BAR")
      (is (true? (components.dialog/is-visible? (sut/dialog-props props)))))))

(deftest test-on-copy!

  (testing "Calls to-clipboard!"
    (let [call (atom nil)
          props (mk-props {kws/to-clipboard! #(reset! call %&)})]
      (sut/on-copy! props)
      (is (= ["FOO"] @call))))

  (testing "Closes the dialog"
    (let [props (mk-props)]
      (sut/on-copy! props)
      (is (false? (components.dialog/is-visible? (sut/dialog-props props)))))))
