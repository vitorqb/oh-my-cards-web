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
  (let [state (atom {kws/card {kws.card/title "A" kws.card/ref 1}})]
    (is
     (tu/exists-in-component?
      [:div.display-card__title "#1 A"]
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

(deftest test-header

  (testing "Renders button with goto-home!"
    (let [state       (atom {})
          props       {:state state kws/goto-home! #(do ::result)}
          header      (sut/header props)
          on-click-fn (-> header second :left second :on-click)]
      (is (= ::result (on-click-fn)))))

  (testing "Renders button with goto-editcard!"
    (let [state       (atom {kws/card {kws.card/id 1}})
          props       {:state state kws/goto-editcard! #(do [::result %])}
          header      (sut/header props)
          on-click-fn (-> header second :center second :on-click)]
      (is (= [::result 1] (on-click-fn))))))

(deftest test-main

  (testing "Loading rendering"
    
    (testing "Display loading-wrapper as second arg"
      (let [state (atom {kws/loading? true})
            [el props] (-> {:state state} sut/main second)]
        (is (= el loading-wrapper/main))
        (is (= {:loading? true} props)))))

  (testing "Non-loading rendering"

    (let [props {:state (atom nil)}
          comp-seq (tu/comp-seq (sut/main props))]

      (testing "Displays title"
        (is (tu/exists-in-component? [sut/title props] comp-seq)))

      (testing "Displays body inside markdown"
        (is (tu/exists-in-component? [sut/body props] comp-seq)))

      (testing "Displays extra info"
        (is (tu/exists-in-component? [sut/extra-info props] comp-seq)))

      (testing "Displays header"
        (is (tu/exists-in-component? [sut/header props] comp-seq))))))
