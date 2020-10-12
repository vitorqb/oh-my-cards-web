(ns ohmycards.web.components.card-history-displayer.field-update-displayer-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.card-history-displayer.field-update-displayer
             :as
             sut]
            [ohmycards.web.kws.common.cards.history.core :as kws.cards.history]
            [ohmycards.web.kws.components.card-history-displayer.field-update-displayer :as kws]))

(deftest test-main
  (testing "Base"
    (let [field-update {kws.cards.history/field-name "Name"
                        kws.cards.history/old-value "1"
                        kws.cards.history/new-value "2"}
          comp (sut/main {kws/field-update field-update})]
      (is (= [:div.field-update-displayer
              [:h4 "Updated " "Name"]
              [:div.field-update-displayer__comparison
               [:pre.field-update-displayer__old-value "\"1\"\n"]
               [:pre.field-update-displayer__new-value "\"2\"\n"]]]
             comp)))))
