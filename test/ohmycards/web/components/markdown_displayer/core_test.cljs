(ns ohmycards.web.components.markdown-displayer.core-test
  (:require [ohmycards.web.components.markdown-displayer.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-update-with-max-width

  (let [base-props {:foo 1 :src "www.google.com/foo&bar=1" :alt "FOO"}]

    (testing "No  MAX_WIDTH in the src attribute"
      (is (= base-props (sut/update-with-max-width base-props))))

    (testing " MAX_WIDTH in the src attribute"
      (let [props (update base-props :alt #(str % " MAX_WIDTH=10px"))]
        (is (= (assoc base-props :style {:max-width "10px"})
               (sut/update-with-max-width props)))))))
