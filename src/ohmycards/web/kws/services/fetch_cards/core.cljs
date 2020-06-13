(ns ohmycards.web.kws.services.fetch-cards.core)


(def cards "An array with the fetched cards." ::cards)
(def error-message "An error message. If set on the response signals a failed fetch."
  ::error-message)

(def config
  "The configuration used to fetch the cards, as of `ohmycards.web.kws.cards-grid.config.core`."
  ::config)
(def search-term "An optional search term to use." ::search-term)

(def page "The current page of fetched cards." ::page)
(def page-size "The current number of cards per page." ::page-size)
(def count-of-cards "The total number of cards available (in the BE)" ::count-of-cards)
