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

(deftest test-success-message
  (let [created-card {kws.card/id "1"}
        path-to! #(str "/#/" (name %1) "?id=" (-> %2 :query-params :id))
        props {:created-card created-card :path-to! path-to!}]

    (testing "Nil if no created-card"
      (let [props (dissoc props :created-card)]
        (is (nil? (sut/success-message props)))))

    (testing "Renders span with text"
      (let [comp (sut/success-message props)]
        (is (tu/exists-in-component? [:span "Created card with uuid "] (tu/comp-seq comp)))))

    (testing "Renders link with href"
      (let [comp (sut/success-message props)]
        (is (tu/exists-in-component? [:a {:href "/#/display-card?id=1"} "1"]
                                     (tu/comp-seq comp)))))))

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
    (let [created-card {kws.card/id "foo"}
          path-to! #(do)
          props {:state (atom {kws/created-card created-card})
                 :path-to! path-to!}]
      (is (tu/exists-in-component?
           [good-message-box/main {:value [sut/success-message {:created-card created-card
                                                                :path-to! path-to!}]}]
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
