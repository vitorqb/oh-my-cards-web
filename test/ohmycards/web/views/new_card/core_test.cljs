(ns ohmycards.web.views.new-card.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.good-message-box.core :as good-message-box]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.leaf :as kws.hydra.leaf]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.new-card.core :as sut]
            [ohmycards.web.views.new-card.handlers.create-card
             :as
             handlers.create-card]
            [ohmycards.web.views.new-card.header :as header]))

(deftest test-main

  (testing "Renders a new-card div in the first place"
    (is (= :div.new-card (-> {:state (atom nil)} sut/main first))))

  (testing "Has header"
    (let [props {:state (atom {})}]
      (is
       (some
        #(= [header/main props] %)
        (tu/comp-seq (sut/main props))))))

  (testing "Has success msg box"
    (let [props {:state (atom {kws/created-card {kws.card/id "foo"}})}]
      (is
       (some
        #(= [good-message-box/main {:value "Created card with uuid foo"}] %)
        (tu/comp-seq (sut/main props))))))

  (testing "Has error box"
    (let [props {:state (atom {kws/error-message "FOO"})}]
      (is
       (some
        #(= [error-message-box/main {:value "FOO"}] %)
        (tu/comp-seq (sut/main props)))))))

(deftest test-hydra-head

  (testing "Create action calls create-card handler"
    (with-redefs [handlers.create-card/main #(do [::create-card %])]
      (let [props {::foo 1}
            create-action (-> props sut/hydra-head kws.hydra.branch/heads first)]
        (is (= [::create-card props] ((kws.hydra.leaf/value create-action))))))))
