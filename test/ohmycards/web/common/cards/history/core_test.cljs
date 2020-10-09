(ns ohmycards.web.common.cards.history.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.cards.history.core :as sut]
            [ohmycards.web.kws.common.cards.history.core :as kws]))

(deftest test-from-http
  (is
   (=
    {kws/events
     [{kws/datetime "2020-10-07T12:19:06.416Z"
       kws/event-type kws/event-update
       kws/field-updates [{kws/field-name "body"
                           kws/field-type kws/field-string
                           kws/old-value "Foo2"
                           kws/new-value "Foo2  \n\nTesting the history!"}
                          {kws/field-name "tags",
                           kws/field-type kws/field-tags,
                           kws/old-value ["Foo2"], 
                           kws/new-value ["Foo2" "Foo3"]}]}]}
    (sut/from-http
     {:history
      [{:datetime "2020-10-07T12:19:06.416Z",
        :eventType "update",
        :fieldUpdates
        [{:fieldName "body",
          :fieldType "string",
          :oldValue "Foo2",
          :newValue "Foo2  \n\nTesting the history!"}
         {:fieldName "tags",
          :fieldType "tags",
          :oldValue ["Foo2"], 
          :newValue ["Foo2" "Foo3"]}]}]}))))
