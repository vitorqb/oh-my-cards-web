(ns ohmycards.web.common.utils
  (:require-macros ohmycards.web.common.utils)
  (:require  [ohmycards.web.globals :as globals]))

(defn to-path [x]
  (cond
    (keyword? x) [x]
    (string?  x) [x]
    (vector?  x) x
    (list?    x) x
    :always (js/console.warn "Can not convert into path!" x)))

(defn str->number [x]
  (when (and (not (= x ""))
             (or (re-find #"^[\\+\\-]?[0-9]*$" x)
                 (re-find #"^[\\+\\-]?[0-9]+\.[0-9]*$" x)
                 (re-find #"^[\\+\\-]?[0-9]*\.[0-9]+$" x)))
    (js/Number x)))

(defn str->integer [x]
  (when (and (not (= x ""))
             (re-find #"^[\\+\\-]?[0-9]*$" x))
    (js/Number x)))

(defn toggle-el-in-set
  "If `el` belongs to set, remove it. Else, add it."
  [set el]
  (if (contains? set el)
    (disj set el)
    (conj set el)))

(defn- internal-link*
  "Generates an internal link to a <path>"
  [{:keys [hostname port]} path]
  (cond-> hostname
    port    (str ":" port)
    :always (str path)))

(def internal-link
  (partial internal-link* {:hostname globals/HOSTNAME :port globals/PORT}))
