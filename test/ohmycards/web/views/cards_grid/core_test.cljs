(ns ohmycards.web.views.cards-grid.core-test
  (:require [ohmycards.web.views.cards-grid.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.views.cards-grid.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.components.error-message-box.core :as error-message-box]))

(deftest test-tags-displayer
  (testing "Renders a span for each tags"
    (let [comp (sut/tags-displayer {::sut/tags ["A" "B"]})]
      (is (tu/exists-in-component? [:span.tags-displayer__tag "A"] comp))
      (is (tu/exists-in-component? [:span.tags-displayer__tag "B"] comp)))))

(deftest test-card-display
  (testing "Renders tags-displayer"
    (is
     (tu/exists-in-component?
      [sut/tags-displayer {::sut/tags ["A"]}]
      (sut/card-display {::sut/card {kws.card/tags ["A"]}})))))

(deftest test-main*

  (testing "Top level component is div with correct class"
    (let [[div] (sut/main* {:state (atom {})})]
      (is (= div :div.cards-grid))))

  (testing "A card has it's card-display rendered"
    (let [card      {kws.card/id "FOO"}
          props     {:state (atom {kws/cards [card]})}
          component (sut/main* props)]
      (is
       (some
        #(= [sut/card-display (assoc props ::sut/card card)] %)
        (tu/comp-seq component)))))

  (testing "Renders error message with error"
    (is
     (some
      #(= [error-message-box/main {:value "Foo"}] %)
      (tu/comp-seq (sut/main* {:state (atom {kws/error-message "Foo"})}))))))
