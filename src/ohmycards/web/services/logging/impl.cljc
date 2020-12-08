(ns ohmycards.web.services.logging.impl)

#?(:cljs
   (do

     (defn new-instance
       "Creates a fresh logging instance."
       []
       (atom {::enabled? false
              ::log-fn (fn [& args] (apply js/console.log args))}))

     (defn set-log-fn!
       [instance log-fn]
       (swap! instance assoc ::log-fn log-fn))

     (defn enable-logging
       "Updates the logging instance to enable logging"
       [instance]
       (swap! instance assoc ::enabled? true))

     (defn disable-logging
       "Updates the logging instance to disable the logging"
       [instance]
       (swap! instance assoc ::enabled? false))

     (defn log!
       "Performs the logging of some msg/obj"
       [instance prefix & args]
       (let [enabled? (::enabled? @instance)
             log-fn   (::log-fn @instance)]
         (when enabled?
           (apply log-fn (str "[" prefix "]") args))))))
