(ns ohmycards.web.common.hydra.core
  (:require [ohmycards.web.kws.hydra.core :as kws.hydra]))

(defn is-leaf? [x] (= (kws.hydra/type x) kws.hydra/leaf))
