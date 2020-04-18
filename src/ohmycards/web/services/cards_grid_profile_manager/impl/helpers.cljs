(ns ohmycards.web.services.cards-grid-profile-manager.impl.helpers
  (:require [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]))

(defn serialize-profile
  "Serializes a profile for an http call."
  [{{::kws.config/keys [page page-size include-tags exclude-tags tags-filter-query]}
    ::kws.profile/config
    name
    ::kws.profile/name}]
  {:name name
   :config {:page page
            :pageSize page-size
            :includeTags include-tags
            :excludeTags exclude-tags
            :query tags-filter-query}})
