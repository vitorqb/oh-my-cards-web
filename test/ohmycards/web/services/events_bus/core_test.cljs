(ns ohmycards.web.services.events-bus.core-test
  (:require [ohmycards.web.services.events-bus.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-send!

  (testing "Dont send without handler"
    (binding [sut/*handler* nil]
      (is (nil? (sut/send! ::event ::args)))))

  (testing "With handler"    

    (binding [sut/*handler* #(do [%1 %2])]

      (testing "Don't send if no event-kw"
        (is (nil? (sut/send! nil ::args))))

      (testing "Send if arg and handler"
        (is (= [::event ::args] (sut/send! ::event ::args)))))))
