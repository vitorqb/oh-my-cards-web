(ns ohmycards.web.services.login.recover-token-from-cookie-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.services.login.recover-token-from-cookie :as sut]))

(deftest test-extract-token-from-token-recovery-response

  (let [state {:foo :bar}
        response {::kws.http/body {:value "foo"} ::kws.http/success? true}]

    (testing "Nil if no body"
      (let [response* (assoc response ::kws.http/body nil)]
        (is (nil? (sut/extract-token-from-token-recovery-response response*)))))

    (testing "Nil if not success"
      (let [response* (assoc response ::kws.http/success? false)]
        (is (false? (sut/extract-token-from-token-recovery-response response*)))))

    (testing "Assocs login"
      (is (= {:value "foo"} (sut/extract-token-from-token-recovery-response response))))))
