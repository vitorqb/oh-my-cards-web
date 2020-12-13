(ns ohmycards.web.kws.views.cards-grid.core)

(def ^:const error-message "User friendly error msg" ::error-message)
(def ^:const fetch-cards! "The function/service used to fetch cards." ::fetch-cards!)
(def ^:const cards "An array with all fetched cards." ::cards)
(def ^:const config "The cards grid config, as of `kws.cards-grid.config.core`" ::config)
(def ^:const filter-enabled? "Boolean indicating if the filter is enabled or not." ::filter-enabled?)

(def ^:const filter-input-search-term "Search term currently on the filter input." ::filter-input-search-term)
(def ^:const search-term "The COMMITTED search term that the user wants to search." ::search-term)

(def ^:const count-of-cards "The total number of cards available for the state." ::count-of-cards)

(def ^:const status "A keyword indicating the current status for the state." ::status)
(def ^:const status-ready "A keyword indicating the state management is done.." ::ready)
(def ^:const status-error "A keyword indicating the state management is finished with error" ::error)
(def ^:const status-loading "A keyword indicating that the state is loading." ::loading)

(def ^:const goto-settings! "A fn that routes the application to the grid settings page" ::goto-settings!)
(def ^:const goto-newcard! "A fn that routes the application to the page to create a new card"
  ::goto-newcard!)
(def ^:const goto-profiles! "A fn that routes the application to the page of profiles" ::goto-profiles!)
(def ^:const goto-findcard! "A fn that routes the application to the page to find a card" ::goto-findcard!)
