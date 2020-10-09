(ns ohmycards.web.views.about.core-test
  (:require [cljs.core.async :as async]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.spinner.core :as spinner]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.about.core :as sut]))

(deftest test-main
  (testing "Renders the about-textbox"
    (with-redefs [sut/fetch-be-version! #(do)]
      (is
       (tu/exists-in-component?
        [sut/about-textbox]
        (tu/comp-seq ((sut/main {}))))))))

(deftest test-fetch-be-version!
  (let [props {:fetch-be-version! #(async/go "FOO")}]
    (async
     done
     (async/go
       (let [a (atom nil)]
         (async/<! (sut/fetch-be-version! props a))
         (is (= @a "FOO"))
         (done))))))

(deftest test-version-label

  (testing "With version"
    (is (= [:div "FOO: bar"]
           (sut/version-label {::sut/text "FOO: " ::sut/version "bar"}))))

  (testing "Without version"
    (is (= [:div "FOO: " [spinner/smallest]]
           (sut/version-label {::sut/text "FOO: " ::sut/version nil})))))

(deftest test-versions-infobox
  (let [be-version "2.2"
        fe-version "1.1"
        comp-seq   (tu/comp-seq
                    (sut/versions-infobox
                     {::sut/be-version be-version ::sut/fe-version fe-version}))]

    (testing "Renders BE version"
      (is (tu/exists-in-component?
           [sut/version-label {::sut/text "Server: " ::sut/version be-version}]
           comp-seq)))

    (testing "Renders FE version"
      (is (tu/exists-in-component?
           [sut/version-label {::sut/text "Web: " ::sut/version fe-version}]
           comp-seq)))))
