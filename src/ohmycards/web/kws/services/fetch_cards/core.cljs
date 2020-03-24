(ns ohmycards.web.kws.services.fetch-cards.core)


(def cards "An array with the fetched cards." ::cards)
(def error-message "An error message. If set on the response signals a failed fetch."
  ::error-message)
(def page "The current page of fetched cards." ::page)
(def page-size "The current number of cards per page." ::page-size)
(def include-tags "Tags that must be on the fetched cards" ::include-tags)
(def count-of-cards "The total number of cards available (in the BE)" ::count-of-cards)
