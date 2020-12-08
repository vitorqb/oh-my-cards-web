(ns ohmycards.web.services.logging.core
  #?(:cljs (:require-macros [ohmycards.web.services.logging.core]))
  (:require [ohmycards.web.services.logging.impl :as impl]))

#?(:cljs
   (do

     (def ^:private logging
       "The main state containing the logging instance."
       (impl/new-instance))

     (defn enable-logging!
       "Enables logging"
       []
       (impl/enable-logging logging))

     (defn disable-logging!
       "Disables logging"
       []
       (impl/disable-logging logging)))

   :clj
   (do
     (defmacro deflogger
       "Creates a new logging function with a given prefix"
       [name prefix]
       `(defn- ~name [& x#] (apply impl/log! logging ~prefix x#)))))
