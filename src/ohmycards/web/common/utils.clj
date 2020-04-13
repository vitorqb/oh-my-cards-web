(ns ohmycards.web.common.utils)

(defmacro with-out-chan
  "Executes `body` with `chan-name` bound to a new channel. Returns the chanel."
  [[chan-name] & body]
  `(let [~chan-name (cljs.core.async/chan)]
     ~@body
     ~chan-name))
