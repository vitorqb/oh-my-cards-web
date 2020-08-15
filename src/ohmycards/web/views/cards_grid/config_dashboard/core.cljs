(ns ohmycards.web.views.cards-grid.config-dashboard.core
  (:require [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.common.coercion.core :as coercion]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.common.tags.core :as tags]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.header.core :as header]
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
(defn- title [x] [:span.cards-grid-config-dashboard__title x])

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
  (let [path          [kws/config kws.config/page]
        set-btn-props {:state  state
                       :path   path
                       :set-fn #(-> @state (get-in path) kws.coercion.result/value set-page!)}
        set-btn       [set-btn set-btn-props]
        input-props   (assoc (input-props state path positive-int-or-nil-coercer)
                             :class "simple-form__input simple-form__input--small")
        input         [:<> [form.input/main input-props] set-btn]]
    [form/row {:label "Page" :input input}]))

(defn- page-size-config
  "A config input for page size"
  [{:keys [state] ::kws/keys [set-page-size!]}]
  (let [path          [kws/config kws.config/page-size]
        set-btn-props {:state  state
                       :path   path
                       :set-fn #(-> @state (get-in path) kws.coercion.result/value set-page-size!)}
        set-btn       [set-btn set-btn-props]
        input-props   (-> (input-props state path positive-int-or-nil-coercer)
                          (assoc :class "simple-form__input simple-form__input--small"))
        input         [:<> [form.input/main input-props] set-btn]]
    [form/row {:label "Page Size" :input input}]))

(defn- include-tags-config
  "A config for tags to include"
  [{:keys [state] ::kws/keys [set-include-tags! cards-metadata]}]
  (let [all-tags      (kws.card-metadata/tags cards-metadata)
        path          [kws/config kws.config/include-tags]
        set-fn        #(do (swap! state update-in path coercion.result/copy-value-to-raw-value)
                           (-> @state (get-in path) kws.coercion.result/value set-include-tags!))
        set-btn-props {:state  state
                       :path   path
                       :set-fn set-fn}
        set-btn       [set-btn set-btn-props]
        input-props   (-> (input-props state path coercers/tags)
                          (assoc kws.inputs.tags/all-tags all-tags))
        input         [:<> [inputs.tags/main input-props] set-btn]]
    [form/row {:label "ALL tags" :input input}]))

(defn- exclude-tags-config
  "A config for tags to exclude"
  [{:keys [state] ::kws/keys [set-exclude-tags! cards-metadata]}]
  (let [all-tags      (kws.card-metadata/tags cards-metadata)
        path          [kws/config kws.config/exclude-tags]
        input-props   (-> (input-props state path coercers/tags)
                          (assoc kws.inputs.tags/all-tags all-tags))
        set-fn        #(do (swap! state update-in path coercion.result/copy-value-to-raw-value)
                           (-> @state (get-in path) kws.coercion.result/value set-exclude-tags!))
        set-btn-props {:state  state
                       :path   path
                       :set-fn set-fn}
        input [:<> [inputs.tags/main input-props] [set-btn set-btn-props]]]
    [form/row {:label "NONE OF tags" :input input}]))

(defn- tags-filter-query-config
  "A config for the tags filter query."
  [{:keys [state] ::kws/keys [set-tags-filter-query!]}]
  (let [path          [kws/config kws.config/tags-filter-query]
        input-props   (input-props state path coercers/string)
        set-fn        #(-> @state (get-in path) kws.coercion.result/value set-tags-filter-query!)
        set-btn-props {:state  state
                       :path   path
                       :set-fn set-fn}
        input [:<> [inputs.textarea/main input-props] [set-btn set-btn-props]]]
    [form/row {:label "Tag Filter Query" :input input}]))

(defn- load-profile-name
  "A row for the user to load a profile by it's name."
  [{:keys [state] ::kws/keys [profiles-names load-profile!]}]
  (let [path          [kws/load-profile-name]
        options       (inputs.combobox/seq->options profiles-names)
        set-btn-props {:label  "Load!"
                       :state  state
                       :path   path
                       :set-fn #(-> @state (get-in path) kws.coercion.result/value load-profile!)}
        input-props   (-> (input-props state path (coercers/is-in profiles-names))
                          (assoc kws.combobox/options options))
        input         [:<> [inputs.combobox/main input-props] [set-btn set-btn-props]]]
    [form/row {:label "Load Profile" :input input}]))

(defn- save-profile-name
  "A row for the user to save a profile by it's name."
  [{:keys [state] ::kws/keys [save-profile!]}]
  (let [path             [kws/save-profile-name]
        profile-for-save (get-profile-for-save @state)
        input-props      (input-props state path string-with-min-len-2)
        set-btn-props    {:label  "Save!"
                          :state  state
                          :path   path
                          :set-fn #(save-profile! profile-for-save)}
        extra-comp       (if profile-for-save
                           [set-btn set-btn-props]
                           [error-message-box/main {:value "Invalid values prevent save!"}])
        input            [:<> [form.input/main input-props] extra-comp]]
    [form/row {:label "Save Profile" :input input}]))

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
