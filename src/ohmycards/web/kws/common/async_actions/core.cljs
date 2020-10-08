(ns ohmycards.web.kws.common.async-actions.core)

(def state "The atom-like containing the state." ::state)
(def run-condition-fn "A 1-arg function that receives the dereferred state and returns a channel with `true` if the action should be run. If the channel is closed or receives false, the entire action is aborted and is not run. Notice this is run before any other hook/reducer." ::run-condition-fn)
(def pre-reducer-fn "A 1-arg fn used to reduce the state before the action." ::pre-reducer-fn)
(def pre-hook-fn "A 0-arg fn called before the action." ::pre-hook-fn)
(def post-reducer-fn "A 2-arg fn used to reduce the state after the action. Receives the payload from the action." ::post-reducer-fn)
(def post-hook-fn "A 1-arg fn called after the action with the result." ::post-hook-fn)
(def action-fn "A 1-arg fn that runs the action. It receives the dereferred state and must return a channel with the response." ::action-fn)
