(ns ohmycards.web.views.profiles.core
  (:require [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.combobox :as inputs.combobox]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.cards-grid.metadata.core :as kws.cards-grid.metadata]
            [ohmycards.web.kws.components.inputs.combobox.core
             :as
             kws.inputs.combobox]
            [ohmycards.web.kws.components.inputs.combobox.options
             :as
             kws.inputs.combobox.options]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.views.profiles.core :as kws]
            [reagent.core :as r]))

;; Helpers
(defn- on-submit
  "Handler for when the user submits the profile."
  [{::kws/keys [load-profile! goto-grid!] :keys [state]}]
  (-> @state ::profile-name load-profile!)
  (goto-grid!))

;; Main API
(defn main
  "A page for managing profiles."
  [{:keys [state] ::kws/keys [profile-names] :as props}]
  (let [profile-name-cursor (r/cursor state [::profile-name])
        combobox-opts (inputs.combobox/seq->options profile-names)]
    [:div.profiles
     [:h2 "Profiles"]
     [form/main {::form/on-submit #(on-submit props)}
      [inputs/main {kws.inputs/itype kws.inputs/t-combobox
                    kws.inputs/cursor profile-name-cursor
                    kws.inputs/props {kws.inputs.combobox/options combobox-opts}
                    kws.inputs/auto-focus true}]
      [form/submit]]]))
