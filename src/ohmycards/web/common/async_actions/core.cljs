(ns ohmycards.web.common.async-actions.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.kws.common.async-actions.core :as kws]
            [ohmycards.web.utils.logging :as logging]))

(logging/deflogger log "Common.AsyncActions")

(defn run
  "Runs an async action."
  [async-action]
  (let [state (kws/state async-action)
        run-condition-fn (or (kws/run-condition-fn async-action)
                             (fn [_] (a/go true)))
        pre-reducer-fn (kws/pre-reducer-fn async-action)
        pre-hook-fn (kws/pre-hook-fn async-action)
        post-reducer-fn (kws/post-reducer-fn async-action)
        post-hook-fn (kws/post-hook-fn async-action)
        action-fn (kws/action-fn async-action)]
    (a/go
      (when (a/<! (run-condition-fn @state))
        (log "Running action: " async-action)
        (when pre-hook-fn
          (log "Running pre-hook for action: " async-action)
          (pre-hook-fn))
        (when pre-reducer-fn
          (log "Running pre-reducer-fn for action: " async-action)
          (swap! state pre-reducer-fn))
        (let [result (a/<! (action-fn @state))]
          (when post-hook-fn
            (log "Running post-hook-fn for action and result: " [async-action result])
            (post-hook-fn result))
          (when post-reducer-fn
            (log "Running post-reducer-fn for action and result: " [async-action result])
            (swap! state post-reducer-fn result)))))))
