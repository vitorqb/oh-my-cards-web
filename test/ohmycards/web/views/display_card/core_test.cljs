(ns ohmycards.web.views.display-card.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.views.display-card.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.display-card.core :as sut]
            [ohmycards.web.components.markdown-displayer.core
             :as
             markdown-displayer]))

(deftest test-title
  (let [state (atom {kws/card {kws.card/title "A"}})]
    (is
     (tu/exists-in-component?
      [:div.display-card__title "A"]
      (tu/comp-seq (sut/title {:state state}))))))

(deftest test-id
  (let [state (atom {kws/card {kws.card/id "A"}})]
    (is
     (tu/exists-in-component?
      [:div.display-card__id "A"]
      (tu/comp-seq (sut/id {:state state}))))))

(deftest test-body
  (let [state (atom {kws/card {kws.card/body "A"}})]
    (is
     (tu/exists-in-component?
      [:div.display-card__body [markdown-displayer/main {:source "A"}]]
      (tu/comp-seq (sut/body {:state state}))))))

(deftest text-extra-info
  (let [state     (atom {kws/card {kws.card/created-at "A" kws.card/updated-at "B"}})
        component (tu/comp-seq (sut/extra-info {:state state}))]

    (testing "Renders created at"
      (is
       (tu/exists-in-component?
        [:span.display-card__extra-info-label.display-card__extra-info-label--left
         "created A"]
        component)))

    (testing "Renders updated at"
      (is
       (tu/exists-in-component?
        [:span.display-card__extra-info-label.display-card__extra-info-label--right
         "updated B"]
        component)))))

(deftest test-main

  (testing "Display loading-wrapper as second arg"
    (let [state (atom {kws/loading? true})
          [el props] (-> {:state state} sut/main second)]
      (is (= el loading-wrapper/main))
      (is (= {:loading? true} props))))

  (testing "Displays title"
    (let [props {:state (atom nil)}]
      (is
       (tu/exists-in-component?
        [sut/title props]
        (tu/comp-seq (sut/main props))))))

  (testing "Displays body inside markdown"
    (let [props {:state (atom nil)}]
      (is
       (tu/exists-in-component?
        [sut/body props]
        (tu/comp-seq (sut/main props))))))

  (testing "Displays extra info"
    (let [props {:state (atom nil)}]
      (is
       (tu/exists-in-component?
        [sut/extra-info props]
        (tu/comp-seq (sut/main props)))))))
