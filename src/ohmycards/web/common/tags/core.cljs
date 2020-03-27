(ns ohmycards.web.common.tags.core)

(defn sanitize
  "Sanitizes a list of tags"
  [tags]
  (remove empty? tags))
