(ns ohmycards.web.kws.hydra.core
  (:refer-clojure :exclude [type]))

;; General
(def shortcut "The shortcut for this the hydra head" ::shortcut)
(def description "The description for the hydra head" ::description)
(def type "The type for the hydra head, either ::branch or ::leaf" ::type)

;; Types
(def branch "Heads of type `::branch` have sub-heads inside" ::branch)
(def leaf "Heads of type `::leaf` have a value inside" ::leaf)
