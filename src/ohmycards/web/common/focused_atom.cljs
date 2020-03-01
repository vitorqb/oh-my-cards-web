(ns ohmycards.web.common.focused-atom
  (:require
   [com.rpl.specter :as s]))

(deftype FocusedAtom [atom path]

  ISwap
  (-swap! [o f]
    (->> (swap! atom (fn [value] (s/transform path #(f %) value)))
         (s/select-first path)))

  (-swap! [o f a]
    (->> (swap! atom (fn [value] (s/transform path #(f % a) value)))
         (s/select-first path)))

  (-swap! [o f a b]
    (->> (swap! atom (fn [value] (s/transform path #(f % a b) value)))
         (s/select-first path)))

  (-swap! [o f a b xs]
    (->> (swap! atom (fn [value] (s/transform path #(apply f % a b xs) value)))
         (s/select-first path)))

  IReset
  (-reset! [o new-value]
    (-swap! o (constantly new-value)))

  IDeref
  (-deref [o]
    (s/select-first path @atom)))
