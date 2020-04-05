(ns ohmycards.web.components.hydra.core
  (:require [ohmycards.web.common.hydra.core :as hydra]
            [ohmycards.web.components.form.input :as input]
            [ohmycards.web.kws.components.hydra.core :as kws]
            [ohmycards.web.kws.hydra.branch :as kws.hydra.branch]
            [ohmycards.web.kws.hydra.core :as kws.hydra]))

;; Helpers
(defn- state-watcher-for-leaf-selection
  "Returns a watcher fn that checks if the currently selected hydra is a leaf, and
  reacts calling the `on-leaf-selection` if it is."
  [{::kws/keys [head on-leaf-selection]}]
  (fn [_ _ _ new-state]
    (when-let [new-path (kws/path new-state)]
      (when-let [new-head (hydra/get-current-head new-path head)]
        (when (hydra/is-leaf? new-head)
          (on-leaf-selection new-head))))))

;; Components
(defn- head-component
  "A component that display a single head for the user."
  [{{::kws.hydra/keys [type] ::kws.hydra.branch/keys [heads]} ::head}]
  (into
   [:div.hydra__head]
   (for [{::kws.hydra/keys [shortcut description]} heads]
     [:div (str "[" shortcut "]: " description)])))

(defn main
  "A hydra is a component that allows the user to select an action using the keyboard, with
  optional groupping and descriptions."
  [{:keys [state] ::kws/keys [head on-leaf-selection] :as props}]
  (remove-watch state ::leaf-selection)
  (add-watch state ::leaf-selection (state-watcher-for-leaf-selection props))
  (let [current-head (hydra/get-current-head (kws/path @state) head)]
    [:div.hydra
     [input/main (input/build-props state [kws/path] :class "hydra__input" :auto-focus true)]
     [head-component (assoc props ::head current-head)]]))
