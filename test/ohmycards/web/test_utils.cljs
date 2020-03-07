(ns ohmycards.web.test-utils
  (:require  [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(defn comp-seq
  "Returns a lazy sequence of all hiccups forms on a components."
  [c]
  (tree-seq #(and (not (string? %)) (seqable? %)) identity c))
