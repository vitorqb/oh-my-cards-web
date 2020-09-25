(ns ohmycards.web.components.current-view.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.current-view.core :as sut]
            [ohmycards.web.kws.user :as kws.user]))

(deftest test-main

  (testing "Returns view if has user"
    (is (= [:div.current-view '([::header-component] [::view])]
           (sut/main {::sut/current-user     {kws.user/email "A" kws.user/token "B"}
                      ::sut/view             ::view
                      ::sut/header-component ::header-component}))))

  (testing "Returns login-page if no user"
    (is (= [:div.current-view [::login-view]]
           (sut/main {::sut/current-user {}
                      ::sut/login-view   ::login-view}))))

  (testing "Renders nothing if we are loading"
    (is (nil? (sut/main {::sut/loading? true})))))
