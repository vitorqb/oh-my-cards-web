(ns ohmycards.web.icons
  (:refer-clojure :exclude [filter]))

(defn arrow-left [p] [:i.fa.fa-arrow-alt-circle-left p])
(defn arrow-right [p] [:i.fa.fa-arrow-alt-circle-right p])
(defn settings [p] [:i.fa.fa-cog p])
(defn add [p] [:i.fa.fa-plus p])
(defn home [p] [:i.fa.fa-home p])
(defn check [p] [:i.fa.fa-check-square p])
(defn edit [p] [:i.fa.fa-edit p])
(defn close [p] [:i.fa.fa-window-close p])
(defn trash [p] [:i.fa.fa-trash p])
(defn refresh [p] [:i.fas.fa-sync-alt p])
(defn filter [p] [:i.fas.fa-filter p])
