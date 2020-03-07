(ns ohmycards.web.utils.components)

(defn with-seq-keys
  "Adds keys to each element in components."
  [components]
  (for [[c i] (map vector components (range))]
    (with-meta c {:key i})))
