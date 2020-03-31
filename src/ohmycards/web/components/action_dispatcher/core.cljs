(ns ohmycards.web.components.action-dispatcher.core
  (:require [ohmycards.web.components.form.input :as input]
            [ohmycards.web.components.hydra.core :as hydra]
            [ohmycards.web.kws.components.action-dispatcher.core :as kws]
            [ohmycards.web.kws.components.hydra.core :as kws.components.hydra]
            [ohmycards.web.kws.hydra.leaf :as kws.hydra.leaf]
            [reagent.core :as r]))

;; Helpers
(defn- gen-hydra-state [{:keys [state]}] (r/cursor state [::hydra]))

;; Components
(defn- hydra
  "The main hydra with the actions."
  [{:keys [state] ::kws/keys [actions-hydra-head dispatch-action!] :as props}]
  [hydra/main {kws.components.hydra/head actions-hydra-head
               kws.components.hydra/on-leaf-selection #(-> % kws.hydra.leaf/value dispatch-action!)
               :state (gen-hydra-state props)}])

;; API
(defn main
  "An action dispatcher, which prompts the user for actions and allows it to choose among them."
  [{:keys [state] :as props}]
  [:div.action-dispatcher
   [:div.action-dispatcher__hydra-wrapper
    [hydra props]]])
