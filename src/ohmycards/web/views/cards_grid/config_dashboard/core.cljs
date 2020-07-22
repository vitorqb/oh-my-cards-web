(ns ohmycards.web.views.cards-grid.config-dashboard.core
  (:require [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.common.coercion.core :as coercion]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.common.tags.core :as tags]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.inputs.combobox :as inputs.combobox]
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
            [ohmycards.web.kws.components.inputs.tags :as kws.inputs.tags]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]))

;; Helpers
(defn- label [x] [:span.cards-grid-config-dashboard__label x])

(defn- set-btn
  "Renders a `set` button to set the value, or an error message if the coercion for the value
  failed.
  `state`: The state.
  `path`: A path inside the state with a coercion result for the input.
  `set-fn`: 0-arg callback called to set the value."
  [{:keys [state path set-fn label]}]
  (if-let [err-msg (-> @state (get-in path) kws.coercion.result/error-message)]
    [error-message-box/main {:value err-msg}]
    [:div.cards-grid-config-dashboard__set-wrapper
     [:button.cards-grid-config-dashboard__set {:on-click #(set-fn)}
      (or label "Set")]]))

(defn- input-wrapper
  [_ & children]
  (into [:div.cards-grid-config-dashboard__input-wrapper] children))

(defn input-props
  "Applies smart default to input props before sending it to `input/build-props`."
  [state path coercer & args]
  (apply form.input/build-props state path
         :class "cards-grid-config-dashboard__input"
         :parse-fn #(coercion/main % coercer)
         :unparse-fn kws.coercion.result/raw-value
         args))

(defn- header
  "A header with options"
  [{::kws/keys [goto-cards-grid!]}]
  [:div.cards-grid-config-dashboard__header
   [:button.icon-button {:on-click #(goto-cards-grid!)}
    [icons/arrow-left]]])

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
  (let [path [kws/config kws.config/page]]
    [:div.cards-grid-config-dashboard__row
     (label "Page")
     [input-wrapper {}
      [form.input/main (input-props state path positive-int-or-nil-coercer)]]
     [set-btn {:state state
               :path path
               :set-fn #(-> @state (get-in path) kws.coercion.result/value set-page!)}]]))

(defn- page-size-config
  "A config input for page size"
  [{:keys [state] ::kws/keys [set-page-size!]}]
  (let [path [kws/config kws.config/page-size]]
    [:div.cards-grid-config-dashboard__row
     (label "Page Size")
     [input-wrapper {}
      [form.input/main (input-props state path positive-int-or-nil-coercer)]]
     [set-btn {:state state
               :path path
               :set-fn #(-> @state (get-in path) kws.coercion.result/value set-page-size!)}]]))

(defn- include-tags-config
  "A config for tags to include"
  [{:keys [state] ::kws/keys [set-include-tags! cards-metadata]}]
  (let [path [kws/config kws.config/include-tags]]
    [:div.cards-grid-config-dashboard__row
     (label "ALL tags")
     [input-wrapper {}
      [inputs.tags/main (assoc (input-props state path coercers/tags)
                               kws.inputs.tags/all-tags (kws.card-metadata/tags cards-metadata))]]
     [set-btn
      {:state state
       :path path
       :set-fn #(do (swap! state update-in path coercion.result/copy-value-to-raw-value)
                    (-> @state (get-in path) kws.coercion.result/value set-include-tags!))}]]))

(defn- exclude-tags-config
  "A config for tags to exclude"
  [{:keys [state] ::kws/keys [set-exclude-tags! cards-metadata]}]
  (let [path [kws/config kws.config/exclude-tags]]
    [:div.cards-grid-config-dashboard__row
     (label "Not ANY tags")
     [input-wrapper {}
      [inputs.tags/main (assoc (input-props state path coercers/tags)
                               kws.inputs.tags/all-tags (kws.card-metadata/tags cards-metadata))]]
     [set-btn
      {:state state
       :path path
       :set-fn #(do (swap! state update-in path coercion.result/copy-value-to-raw-value)
                    (-> @state (get-in path) kws.coercion.result/value set-exclude-tags!))}]]))

(defn- tags-filter-query-config
  "A config for the tags filter query."
  [{:keys [state] ::kws/keys [set-tags-filter-query!]}]
  (let [path [kws/config kws.config/tags-filter-query]]
    [:div.cards-grid-config-dashboard__row
     (label "Tag Filter Query")
     [input-wrapper {}
      [inputs.textarea/main (input-props state path coercers/string)]]
     [set-btn
      {:state state
       :path path
       :set-fn #(-> @state (get-in path) kws.coercion.result/value set-tags-filter-query!)}]]))

(defn- load-profile-name
  "A row for the user to load a profile by it's name."
  [{:keys [state] ::kws/keys [profiles-names load-profile!]}]
  (let [path [kws/load-profile-name]
        options (inputs.combobox/seq->options profiles-names)]
    [:div.cards-grid-config-dashboard__row
     (label "Load Profile")
     [input-wrapper {}
      [inputs.combobox/main (input-props state path (coercers/is-in profiles-names)
                                         kws.combobox/options options)]]
     [set-btn
      {:label "Load!"
       :state state
       :path path
       :set-fn #(-> @state (get-in path) kws.coercion.result/value load-profile!)}]]))

(defn- save-profile-name
  "A row for the user to save a profile by it's name."
  [{:keys [state] ::kws/keys [save-profile!]}]
  (let [path [kws/save-profile-name]]
    [:div.cards-grid-config-dashboard__row
     (label "Save Profile")
     [input-wrapper {}
      [form.input/main (input-props state path string-with-min-len-2)]]
     (if-let [profile-for-save (get-profile-for-save @state)]
       [set-btn
        {:label "Save!"
         :state state
         :path path
         :set-fn #(save-profile! profile-for-save)}]
       [error-message-box/main {:value "Invalid values prevent save!"}])]))

(defn- profile-manager
  "A profile manager for the cards grid configuration."
  [{:keys [state] :as props}]
  [:div.cards-grid-config-dashboard__profile-manager
   (label "Profile Manager")
   [load-profile-name props]
   [save-profile-name props]])

;; Main
(defn main
  "A configuration dashboard for the cards grid."
  [props]
  [:div.cards-grid-config-dashboard
   [header props]
   [page-config props]
   [page-size-config props]
   [include-tags-config props]
   [exclude-tags-config props]
   [tags-filter-query-config props]
   [profile-manager props]])
