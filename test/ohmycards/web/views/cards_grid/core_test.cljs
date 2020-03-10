(ns ohmycards.web.views.cards-grid.core-test
  (:require [ohmycards.web.views.cards-grid.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.views.cards-grid.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.components.error-message-box.core :as error-message-box]))

(deftest test-main*

  (testing "Top level component is div with correct class"
    (let [[div] (sut/main* {:state (atom {})})]
      (is (= div :div.cards-grid))))

  (testing "A card has it's card-display rendered"
    (let [card      {kws.card/id "FOO"}
          component (sut/main* {:state (atom {kws/cards [card]})})]
      (is
       (some
        #(= [sut/card-display {::sut/card card}] %)
        (tu/comp-seq component)))))

  (testing "Renders error message with error"
    (is
     (some
      #(= [error-message-box/main {:value "Foo"}] %)
      (tu/comp-seq (sut/main* {:state (atom {kws/error-message "Foo"})}))))))
