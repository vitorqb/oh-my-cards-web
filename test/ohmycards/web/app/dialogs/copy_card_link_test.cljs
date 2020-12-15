(ns ohmycards.web.app.dialogs.copy-card-link-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.app.dialogs.copy-card-link :as sut]
            [ohmycards.web.components.copy-card-link-dialog.core
             :as
             components.copy-card-link-dialog]))

(deftest test-dialog

  (testing "Renders dialog with props"
    (let [props {::foo 1}]
      (binding [sut/*props* props]
        (let [result (sut/dialog)]
          (is (= [components.copy-card-link-dialog/main props] result)))))))
