(ns ohmycards.web.utils.pagination)

(defn last-page
  "Returns the last page given a page size and count of items."
  [page-size count-of-items]
  (js/Math.ceil (/ count-of-items page-size)))
