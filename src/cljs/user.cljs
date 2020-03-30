(ns cljs.user
  (:require [cljs.repl :refer [doc]]
            [cljs.test :refer-macros [run-tests]]
            [ohmycards.web.core :as core]
            test-ns-requires))

;; App shortcuts
(def state core/state)
