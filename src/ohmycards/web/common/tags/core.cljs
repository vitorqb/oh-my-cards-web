(ns ohmycards.web.common.tags.core)

(defn sanitize
  "Sanitizes a list of tags"
  [tags]
  (remove empty? tags))

(defn valid?
  "Is it a valid tag?"
  [tag]
  (and (not (empty? tag))
       (not (some #{\space} tag))))
