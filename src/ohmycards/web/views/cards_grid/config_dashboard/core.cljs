(ns ohmycards.web.views.cards-grid.config-dashboard.core
  (:require [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.common.coercion.core :as coercion]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.common.tags.core :as tags]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.header.core :as header]
            [ohmycards.web.components.inputs.combobox :as inputs.combobox]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.components.inputs.simple :as inputs.simple]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.components.inputs.textarea :as inputs.textarea]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.card-metadata :as kws.card-metadata]
            [ohmycards.web.kws.cards-grid.config.core :as kws.config]
            [ohmycards.web.kws.cards-grid.profile.core :as kws.profile]
            [ohmycards.web.kws.common.coercion.result :as kws.coercion.result]
            [ohmycards.web.kws.components.inputs.combobox.core :as kws.combobox]
            [ohmycards.web.kws.components.inputs.combobox.options
             :as
             kws.combobox.options]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.kws.components.inputs.tags :as kws.inputs.tags]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]
            [reagent.core :as r]))

;; Helpers
(defn- title [x] [:span.cards-grid-config-dashboard__title x])

(defn- set-btn
  "Renders a `set` button to set the value, or an error message if the coercion for the value
  failed.
  `cursor`: The cursor-like object containing the value of the input.
  `set-fn`: 0-arg callback called to set the value."
  [{:keys [set-fn label cursor]}]
  (if-let [err-msg (kws.coercion.result/error-message @cursor)]
    [error-message-box/main {:value err-msg}]
    [:div.cards-grid-config-dashboard__set-wrapper
     [:button.cards-grid-config-dashboard__set {:on-click #(set-fn)}
      (or label "Set")]]))

(defn- input-wrapper
  [_ & children]
  (into [:div.cards-grid-config-dashboard__input-wrapper] children))

(defn- header
  "A header with options"
  [{::kws/keys [goto-cards-grid!]}]
  [header/main {:left [:button.icon-button {:on-click #(goto-cards-grid!)}
                       [icons/arrow-left]]}])

(defn- get-profile-for-save
  "Get's the profile from the state ready to be saved."
  [state]
  (if-let [config (some-> state kws/config coercion/extract-values)]
    (let [name (-> state kws/save-profile-name kws.coercion.result/value)]
      {kws.profile/name name kws.profile/config config})))

;; Coercers
(def positive-int-or-nil-coercer
  (coercion/->Or [#(-> % coercers/empty)
                  #(-> % coercers/integer coercers/positive)]))

(def string-with-min-len-2
  #(-> % coercers/string ((coercers/min-length 2))))

;; Inputs
(defn- page-config
  "A config input for a page"
  [{:keys [state] ::kws/keys [set-page!]}]
  (let [cursor (r/cursor state [kws/config kws.config/page])]
    [form/row
     {:label "Page"
      :input [:<>
              [inputs/main
               {kws.inputs/cursor cursor
                kws.inputs/coercer positive-int-or-nil-coercer
                kws.inputs/props {:class "simple-input simple-input--small"}}]
              [set-btn
               {:cursor cursor
                :set-fn #(-> @cursor kws.coercion.result/value set-page!)}]]}]))

(defn- page-size-config
  "A config input for page size"
  [{:keys [state] ::kws/keys [set-page-size!]}]
  (let [cursor (r/cursor state [kws/config kws.config/page-size])]
    [form/row
     {:label "Page Size"
      :input [:<>
              [inputs/main
               {kws.inputs/cursor cursor
                kws.inputs/coercer positive-int-or-nil-coercer
                kws.inputs/props {:class "simple-input simple-input--small"}}]
              [set-btn
               {:cursor cursor
                :set-fn #(-> @cursor kws.coercion.result/value set-page-size!)}]]}]))

(defn- include-tags-config
  "A config for tags to include"
  [{:keys [state] ::kws/keys [set-include-tags! cards-metadata]}]
  (let [all-tags (kws.card-metadata/tags cards-metadata)
        cursor (r/cursor state [kws/config kws.config/include-tags])]
    [form/row
     {:label "ALL tags"
      :input [:<>
              [inputs/main
               {kws.inputs/cursor cursor
                kws.inputs/itype kws.inputs/t-tags
                kws.inputs/coercer coercers/tags
                kws.inputs/props {kws.inputs.tags/all-tags all-tags}}]
              [set-btn
               {:cursor cursor
                :set-fn #(do (swap! cursor coercion.result/copy-value-to-raw-value)
                             (-> @cursor kws.coercion.result/value set-include-tags!))}]]}]))

(defn- exclude-tags-config
  "A config for tags to exclude"
  [{:keys [state] ::kws/keys [set-exclude-tags! cards-metadata]}]
  (let [all-tags (kws.card-metadata/tags cards-metadata)
        cursor (r/cursor state [kws/config kws.config/exclude-tags])]
    [form/row
     {:label "NONE OF tags"
      :input [:<>
              [inputs/main
               {kws.inputs/cursor cursor
                kws.inputs/itype kws.inputs/t-tags
                kws.inputs/coercer coercers/tags
                kws.inputs/props {kws.inputs.tags/all-tags all-tags}}]
              [set-btn
               {:cursor cursor
                :set-fn #(do (swap! cursor coercion.result/copy-value-to-raw-value)
                             (-> @cursor kws.coercion.result/value set-exclude-tags!))}]]}]))

(defn- tags-filter-query-config
  "A config for the tags filter query."
  [{:keys [state] ::kws/keys [set-tags-filter-query!]}]
  (let [cursor (r/cursor state [kws/config kws.config/tags-filter-query])]
    [form/row
     {:label "Tag Filter Query"
      :input [:<>
              [inputs/main
               {kws.inputs/itype kws.inputs/t-textarea
                kws.inputs/cursor cursor
                kws.inputs/coercer coercers/string}]
              [set-btn
               {:cursor cursor
                :set-fn #(-> @cursor kws.coercion.result/value set-tags-filter-query!)}]]}]))

(defn- load-profile-name
  "A row for the user to load a profile by it's name."
  [{:keys [state] ::kws/keys [profiles-names load-profile!]}]
  (let [cursor (r/cursor state [kws/load-profile-name])
        options (inputs.combobox/seq->options profiles-names)]
    [form/row
     {:label "Load Profile"
      :input [:<>
              [inputs/main
               {kws.inputs/itype kws.inputs/t-combobox
                kws.inputs/cursor cursor
                kws.inputs/coercer (coercers/is-in profiles-names)
                kws.inputs/props {kws.combobox/options options}}]
              [set-btn
               {:label "Load!"
                :cursor cursor
                :set-fn #(-> @cursor kws.coercion.result/value load-profile!)}]]}]))

(defn- save-profile-name
  "A row for the user to save a profile by it's name."
  [{:keys [state] ::kws/keys [save-profile!]}]
  (let [profile-for-save (get-profile-for-save @state)
        cursor (r/cursor state [kws/save-profile-name])]
    [form/row
     {:label "Save Profile"
      :input [:<>
              [inputs/main
               {kws.inputs/coercer string-with-min-len-2
                kws.inputs/cursor cursor}]
              (if profile-for-save
                [set-btn
                 {:label  "Save!"
                  :cursor cursor
                  :set-fn #(save-profile! profile-for-save)}]
                [error-message-box/main
                 {:value "Invalid values prevent save!"}])]}]))

(defn- grid-config
  "General configuration for the grid"
  [props]
  [:<> [title "General Configuration"]
   [:div.cards-grid-config-dashboard__grid-config
    [:div.grid__row
     [:div.grid__cell
      [page-config props]]
     [:div.grid__cell
      [page-size-config props]]]
    [include-tags-config props]
    [exclude-tags-config props]
    [tags-filter-query-config props]]])

(defn- profile-manager
  "A profile manager for the cards grid configuration."
  [props]
  [:<>
   [title "Profile Manager"]
   [:div.cards-grid-config-dashboard__profile-manager
    [load-profile-name props]
    [save-profile-name props]]])

;; Main
(defn main
  "A configuration dashboard for the cards grid."
  [props]
  [:div.cards-grid-config-dashboard
   [header props]
   [grid-config props]
   [profile-manager props]])
