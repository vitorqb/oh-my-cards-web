(ns ohmycards.web.components.current-view.core-test
  (:require [ohmycards.web.components.current-view.core :as sut]
            [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(deftest test-main

  (testing "Returns view if has user"
    (is (= [:div.current-view '([::header-component] [::view])]
           (sut/main {::sut/current-user     :user
                      ::sut/view             ::view
                      ::sut/header-component ::header-component}))))

  (testing "Returns login-page if no user"
    (is (= [:div.current-view [::login-view]]
           (sut/main {::sut/login-view ::login-view})))))
