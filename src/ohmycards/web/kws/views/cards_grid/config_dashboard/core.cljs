(ns ohmycards.web.kws.views.cards-grid.config-dashboard.core)

(def goto-cards-grid! "A fn that navigates the app back to the cards grid." ::goto-cards-grid!)
(def set-page! "A fn that sets the page configuration for the grid." ::set-page!)
(def set-page-size! "A fn that sets the page size configuration for the grid." ::set-page-size!)
(def set-include-tags! "A fn that sets the tags that must be included in the cards."
  ::set-include-tags!)
(def set-exclude-tags! "A fn that sets the tags that must NOT be included in the cards."
  ::set-exclude-tags!)

(def config "The cards grid configuration map." ::config)
