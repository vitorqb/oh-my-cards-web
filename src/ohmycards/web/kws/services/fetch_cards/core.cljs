(ns ohmycards.web.kws.services.fetch-cards.core)


(def cards "An array with the fetched cards." ::cards)
(def error-message "An error message. If set on the response signals a failed fetch."
  ::error-message)
;; !!!! TODO All those are repeated from cards-grid config
;; !!!! /home/vitor/mygit/oh-my-cards-web/src/ohmycards/web/kws/cards_grid/config/core.cljs
;; !!!! we should just use it!         
(def page "The current page of fetched cards." ::page)
(def page-size "The current number of cards per page." ::page-size)
(def include-tags "Tags that must be on the fetched cards" ::include-tags)
(def exclude-tags "Tags that must be NOT on the fetched cards" ::exclude-tags)
(def count-of-cards "The total number of cards available (in the BE)" ::count-of-cards)
(def tags-filter-query "A query for filtering the tags." ::tags-filter-query)
