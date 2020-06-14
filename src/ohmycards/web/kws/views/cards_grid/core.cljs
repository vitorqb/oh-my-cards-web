(ns ohmycards.web.kws.views.cards-grid.core)

(def error-message "User friendly error msg" ::error-message)
(def fetch-cards! "The function/service used to fetch cards." ::fetch-cards!)
(def cards "An array with all fetched cards." ::cards)
(def config "The cards grid config, as of `kws.cards-grid.config.core`" ::config)
(def filter-enabled? "Boolean indicating if the filter is enabled or not." ::filter-enabled?)

(def filter-input-search-term "Search term currently on the filter input." ::filter-input-search-term)
(def search-term "The COMMITTED search term that the user wants to search." ::search-term)

(def count-of-cards "The total number of cards available for the state." ::count-of-cards)

(def status "A keyword indicating the current status for the state." ::status)
(def status-ready "A keyword indicating the state management is done.." ::ready)
(def status-error "A keyword indicating the state management is finished with error" ::error)

(def goto-settings! "A fn that routes the application to the grid settings page" ::goto-settings!)
(def goto-newcard! "A fn that routes the application to the page to create a new card"
  ::goto-newcard!)
(def goto-editcard! "A fn that routes the application to the page to edit a card"
  ::goto-editcard!)
