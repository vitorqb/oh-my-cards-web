(ns ohmycards.web.utils.logging)

(defmacro deflogger
  "Creates a new logging function with a given prefix"
  [name prefix]
  `(defn- ~name [& x#] (apply js/console.log ~(str "[" prefix "]") x#)))
