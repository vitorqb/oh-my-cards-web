(ns ohmycards.web.views.new-card.core-test
  (:require [ohmycards.web.views.new-card.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.views.new-card.header :as header]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.kws.views.new-card.core :as kws]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.good-message-box.core :as good-message-box]
            [ohmycards.web.kws.card :as kws.card]))

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
