(ns ohmycards.web.views.cards-grid.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.views.cards-grid.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.cards-grid.control-filter :as control-filter]
            [ohmycards.web.views.cards-grid.core :as sut]))

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

  (testing "If `filter-enabled?` is true, renders the fitlering control."
    (let [props     {:state (atom {kws/filter-enabled? true})}
          component (sut/main* props)]
      (is (tu/exists-in-component? [control-filter/main props] (tu/comp-seq component)))))

  (testing "If `filter-enabled?` is false, DO NOT render the fitlering control."
    (let [props     {:state (atom {kws/filter-enabled? nil})}
          component (sut/main* props)]
      (is (not (tu/exists-in-component? [control-filter/main props] (tu/comp-seq component))))))

  (testing "Renders error message with error"
    (is
     (some
      #(= [error-message-box/main {:value "Foo"}] %)
      (tu/comp-seq (sut/main* {:state (atom {kws/error-message "Foo"})}))))))
