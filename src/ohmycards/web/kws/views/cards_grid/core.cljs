(ns ohmycards.web.kws.views.cards-grid.core)

(def error-message "User friendly error msg" ::error-message)
(def fetch-cards! "The function/service used to fetch cards." ::fetch-cards!)
(def cards "An array with all fetched cards." ::cards)

;; !!!! TODO All those are repeated from cards-grid config
;; !!!! /home/vitor/mygit/oh-my-cards-web/src/ohmycards/web/kws/cards_grid/config/core.cljs
;; !!!! we should just use it!         
(def page "The current page of cards the user is seeing." ::page)
(def page-size "The number of cards per page the user is seeing." ::page-size)
(def include-tags "Tags that all cards must include." ::include-tags)
(def exclude-tags "Tags that all cards must NOT include." ::exclude-tags)
(def tags-filter-query "A query for filtering the tags." ::tags-filter-query)
(def count-of-cards "The total number of cards available for the state." ::count-of-cards)
(def status "A keyword indicating the current status for the state." ::status)

(def status-ready "A keyword indicating the state management is done.." ::ready)
(def status-error "A keyword indicating the state management is finished with error" ::error)

(def goto-settings! "A fn that routes the application to the grid settings page" ::goto-settings!)
(def goto-newcard! "A fn that routes the application to the page to create a new card"
  ::goto-newcard!)
(def goto-editcard! "A fn that routes the application to the page to edit a card"
  ::goto-editcard!)
