(ns ohmycards.web.components.action-dispatcher.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.action-dispatcher.core :as sut]
            [ohmycards.web.components.hydra.core :as hydra]
            [ohmycards.web.kws.components.action-dispatcher.core :as kws]
            [ohmycards.web.kws.components.hydra.core :as kws.components.hydra]
            [ohmycards.web.test-utils :as tu]))

(deftest test-hydra

  (testing "Renders hydra"
    (with-redefs [sut/gen-hydra-state #(do ::state)]
      (let [hydra-head {::foo 1}
            props {kws/actions-hydra-head hydra-head}
            [c hydra-props] (sut/hydra props)]
        (is (= c hydra/main))
        (is (= hydra-head (kws.components.hydra/head hydra-props)))
        (is (= ::state (:state hydra-props)))
        (is (fn? (kws.components.hydra/on-leaf-selection hydra-props)))))))

(deftest test-main

  (testing "Renders hydra"
    (let [props {:foo 1}]
      (is (tu/exists-in-component? [sut/hydra props] (sut/main props))))))
