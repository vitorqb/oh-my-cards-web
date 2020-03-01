(ns cljs.user
  (:require [ohmycards.web.core :as core]
            [cljs.test :refer-macros [run-tests]]
            [test-ns-requires]))

;; App shortcuts
(def state core/state)
