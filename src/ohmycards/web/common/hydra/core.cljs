(ns ohmycards.web.common.hydra.core
  (:require [ohmycards.web.kws.hydra.core :as kws.hydra]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]))

(defn is-leaf? [x] (= (kws.hydra/type x) kws.hydra/leaf))

(defn find-child-for-shortcut
  "Given a head and a char, finds a children with this shortcut."
  [head shorcut]
  (first (filter #(= (str (kws.hydra/shortcut %)) shorcut) (kws.hydra.branch/heads head))))

(defn get-current-head
  "Returns the current head given a path and a root head."
  [path {::kws.hydra/keys [type] :as root}]
  (loop [h root [p & ps] path]
    (cond
      (nil? h) nil
      (nil? p) h
      :else    (recur (find-child-for-shortcut h p) ps))))
