(ns ohmycards.web.test-utils
  (:require  [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(defn comp-seq
  "Returns a lazy sequence of all hiccups forms on a components."
  [c]
  (tree-seq #(and (not (string? %)) (seqable? %)) identity c))

(defn safe-first [x] (and (seqable? x) (first x)))

(defn get-first [pred col] (some #(and (pred %) %) col))
