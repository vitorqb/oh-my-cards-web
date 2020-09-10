(ns ohmycards.web.services.notify-test
  (:require [ohmycards.web.services.notify :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-notify!

  (testing "Set's msg and show on the state"
    (let [state (atom {})]
      (with-redefs [sut/schedule-hide! (fn ([]) ([_]))]
        (sut/notify! "msg" state)
        (is (= "msg" (::sut/msg @state)))
        (is (true? (::sut/show? @state))))))

  (testing "Calls schedule hide"
    (let [state (atom nil)
          calls (atom [])]
      (with-redefs [sut/schedule-hide! (fn ([]) ([x] (swap! calls conj x)))]
        (sut/notify! "" state)
        (is (= [state] @calls))))))

(deftest get-classes
  (is (= "toast" (sut/get-classes {})))
  (is (= "toast toast--show" (sut/get-classes {::sut/show? true}))))
