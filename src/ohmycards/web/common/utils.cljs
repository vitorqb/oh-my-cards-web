(ns ohmycards.web.common.utils)

(defn to-path [x]
  (cond
    (keyword? x) [x]
    (string?  x) [x]
    (vector?  x) x
    (list?    x) x
    :always (js/console.warn "Can not convert into path!" x)))
