(ns ohmycards.web.services.cards-grid-profile-manager.impl.helpers-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.services.cards-grid-profile-manager.impl.helpers :as sut]))

(deftest test-serialize-profile

  (let [config {kws.config/page 1
                kws.config/page-size 2
                kws.config/include-tags ["A"]
                kws.config/exclude-tags ["B"]
                kws.config/tags-filter-query "((tags CONTAINS 'foo'))"}
        profile {kws.profile/name "Name"
                 kws.profile/config config }
        exp-serialized {:name "Name"
                        :config {:page 1
                                 :pageSize 2
                                 :includeTags ["A"]
                                 :excludeTags ["B"]
                                 :query "((tags CONTAINS 'foo'))"}}]

    (is (= exp-serialized (sut/serialize-profile profile)))))
