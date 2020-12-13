(ns ohmycards.web.views.find-card.core-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.services.cards-crud.core :as kws.services.cards-crud]
            [ohmycards.web.kws.views.find-card.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.find-card.core :as sut]
            [reagent.core :as r]))

(defn- mk-props
  [{::kws/keys [value fetch-card! disabled? goto-displaycard! storage-put!]
    :or {value ""
         fetch-card! (constantly nil)
         disabled? false
         goto-displaycard! (constantly nil)
         storage-put! (constantly nil)}}]
  {:state (r/atom {kws/value value
                   kws/disabled? disabled?})
   kws/fetch-card! fetch-card!
   kws/goto-displaycard! goto-displaycard!
   kws/storage-put! storage-put!})

(defn- mk-component [opts] (sut/main (mk-props opts)))

(deftest test-submit-async-action

  (letfn [(mk-action [opts] (sut/submit-async-action (mk-props opts)))]

    (testing "pre-reducer-fn"
      (let [action (mk-action {})
            pre-reducer-fn (kws.async-actions/pre-reducer-fn action)]

        (testing "Set's disabled to true"
          (is (true? (-> {} pre-reducer-fn kws/disabled?))))

        (testing "Unsets err msg"
          (is (nil? (-> {kws/error-message "FOO"} pre-reducer-fn kws/error-message))))))

    (testing "post-reducer-fn"
      (let [action (mk-action {})
            post-reducer-fn (kws.async-actions/post-reducer-fn action)]

        (testing "Set's disabled to false"
          (let [response {kws.services.cards-crud/error-message nil}
                new-state (post-reducer-fn {} response)]
            (is (false? (kws/disabled? new-state)))))

        (testing "Set's error message"
          (let [response {kws.services.cards-crud/error-message "FOO"}
                new-state (post-reducer-fn {} response)]
            (is (= sut/NOT_FOUND_ERR (kws/error-message new-state)))))))

    (testing "post-hook-fn"

      (testing "Sends user to display card on success"
        (let [storage-put! (tu/new-stub {:fn (constantly "key")})
              goto-displaycard! (tu/new-stub)
              action (mk-action {kws/goto-displaycard! goto-displaycard!
                                 kws/storage-put! storage-put!})
              post-hook-fn (kws.async-actions/post-hook-fn action)
              card {kws.card/id "id"}
              response {kws.services.cards-crud/read-card card}]
          (post-hook-fn response)
          (is (= [[card]]
                 (tu/get-calls storage-put!)))
          (is (= [["id" {:storage-key "key"}]]
                 (tu/get-calls goto-displaycard!)))))

      (testing "DOES NOT send user to display card on error"
        (let [goto-displaycard! (tu/new-stub)
              action (mk-action {kws/goto-displaycard! goto-displaycard!})
              post-hook-fn (kws.async-actions/post-hook-fn action)
              response {kws.services.cards-crud/error-message "FOO"}]
          (post-hook-fn response)
          (is (= [] (tu/get-calls goto-displaycard!))))))))

(deftest test-main

  (testing "Renders an input with value from state"
    (let [comp  (mk-component {kws/value "123"})
          input-props (tu/get-props-for inputs/main (tu/comp-seq comp))]
      (is (= (-> input-props kws.inputs/cursor deref) "123"))))

  (testing "Renders a form submit btn"
    (let [comp (mk-component {})]
      (is (tu/exists-in-component? [:input#submit {:type "submit"
                                                   :disabled false}]
                                   (tu/comp-seq comp)))))

  (testing "Handles form submission"
    (let [calls (atom 0)
          comp (mk-component {})
          form-props (tu/get-props-for form/main (tu/comp-seq comp))]
      (with-redefs [sut/handle-submit! #(swap! calls inc)]
        (is (= 0 @calls))
        ((::form/on-submit form-props))
        (is (= 1 @calls)))))

  (testing "Passes disabled to button and input"
    (let [comp (mk-component {kws/disabled? true})
          btn-props (tu/get-props-for :input#submit (tu/comp-seq comp))]
      (is (true? (:disabled btn-props))))))
