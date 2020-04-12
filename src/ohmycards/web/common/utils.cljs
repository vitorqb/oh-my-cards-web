(ns ohmycards.web.common.utils
  (:require-macros ohmycards.web.common.utils))

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
