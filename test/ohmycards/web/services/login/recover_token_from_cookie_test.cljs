(ns ohmycards.web.services.login.recover-token-from-cookie-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.services.login.recover-token-from-cookie :as sut]))

(deftest test-extract-token-from-token-recovery-response

  (let [state {:foo :bar}
        response {::kws.http/body {:value "foo"} ::kws.http/success? true}]

    (testing "Nil"
      (let [response* (assoc response ::kws.http/body nil)]
        (is (= :notoken (sut/extract-token-from-token-recovery-response response*)))))

    (testing "Identity if not success"
      (let [response* (assoc response ::kws.http/success? false)]
        (is (= :notoken (sut/extract-token-from-token-recovery-response response*)))))

    (testing "Assocs login"
      (is (= {:value "foo"} (sut/extract-token-from-token-recovery-response response))))))
