(ns ohmycards.web.common.hydra.core-test
  (:require [ohmycards.web.common.hydra.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]
            [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]))

(deftest test-get-current-head

  (testing "Empty path -> root"
    (let [root {kws.hydra/description "FOO" kws.hydra/type kws.hydra/branch}]
      (is (= root (sut/get-current-head "" root)))
      (is (= root (sut/get-current-head nil root)))))

  (testing "1-long nesting"

    (let [child {kws.hydra/shortcut \c}
          root  {kws.hydra/description  "FOO"
                 kws.hydra/type         kws.hydra/branch
                 kws.hydra.branch/heads [child]}]

      (testing "existing"
        (is (= child (sut/get-current-head "c" root))))

      (testing "not existing"
        (is (= nil (sut/get-current-head "d" root))))))

  (testing "2-long nesting"

    (let [grandchild {kws.hydra/shortcut \b}
          child {kws.hydra/shortcut \c
                 kws.hydra/type kws.hydra/branch
                 kws.hydra.branch/heads [grandchild]}
          root  {kws.hydra/description  "FOO"
                 kws.hydra/type         kws.hydra/branch
                 kws.hydra.branch/heads [child]}]

      (testing "existing"
        (is (= child (sut/get-current-head "c" root)))
        (is (= grandchild (sut/get-current-head "cb" root))))

      (testing "Nested 1 long not existing"
        (is (= nil (sut/get-current-head "d" root)))
        (is (= nil (sut/get-current-head "cd" root)))))))
