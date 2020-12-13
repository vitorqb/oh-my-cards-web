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

(defprotocol IFnStub
  (store-call [this args])
  (get-calls [this]))

(defn new-stub
  "Returns a new callable stub."
  ([] (new-stub {}))
  ([{:keys [fn] :or {fn (constantly nil)}}]
   (let [calls (atom [])]
     (reify
       IFnStub
       (store-call [this args] (swap! calls conj args))
       (get-calls [this] @calls)
       IFn
       (-invoke [this]
         (store-call this [])
         (apply fn []))
       (-invoke [this a]
         (store-call this [a])
         (apply fn [a]))
       (-invoke [this a b]
         (store-call this [a b])
         (apply fn [a b]))
       (-invoke [this a b c]
         (store-call this [a b c])
         (apply fn [a b c]))
       (-invoke [this a b c d]
         (store-call this [a b c d])
         (apply fn [a b c d]))
       (-invoke [this a b c d e]
         (store-call this [a b c d e])
         (apply fn [a b c d e]))
       (-invoke [this a b c d e f]
         (store-call this [a b c d e f])
         (apply fn [a b c d e f]))
       (-invoke [this a b c d e f g]
         (store-call this [a b c d e f g])
         (apply fn [a b c d e f g]))
       (-invoke [this a b c d e f g h]
         (store-call this [a b c d e f g h])
         (apply fn [a b c d e f g h]))))))
