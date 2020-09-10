(ns ohmycards.web.app.state
  (:require [reagent.core :as r]
            [ohmycards.web.common.utils :as utils]))

(defonce state (r/atom {}))

(defn- state-cursor [path] (r/cursor state (utils/to-path path)))
