(ns ohmycards.web.test-utils
  (:require  [cljs.test :refer-macros [is are deftest testing use-fixtures async]]))

(defn comp-seq
  "Returns a lazy sequence of all hiccups forms on a components."
  [c]
  (tree-seq #(and (not (string? %)) (seqable? %)) identity c))

(defn safe-first [x] (and (seqable? x) (first x)))

(defn get-first [pred col] (some #(and (pred %) %) col))

(defn exists-in-component?
  "Returns logical true of a given element is found on the component tree."
  [element component]
  (some #(= element %) (comp-seq component)))

(defn get-props-for
  "Returns THE FIRST props for a component of a `type` t."
  [t component]
  (let [[_ props] (get-first #(= (safe-first %) t) component)]
    props))
