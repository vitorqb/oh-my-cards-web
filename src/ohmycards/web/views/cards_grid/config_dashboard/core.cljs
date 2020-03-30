(ns ohmycards.web.views.cards-grid.config-dashboard.core
  (:require [ohmycards.web.common.coercion.coercers :as coercers]
            [ohmycards.web.common.coercion.core :as coercion]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.common.tags.core :as tags]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.inputs.tags :as inputs.tags]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.kws.common.coercion.result :as kws.coercion.result]
            [ohmycards.web.kws.views.cards-grid.config-dashboard.core :as kws]))

;; Helpers
(defn- label [x] [:span.cards-grid-config-dashboard__label x])

(defn- set-btn
  "Renders a `set` button to set the value, or an error message if the coercion for the value
  failed.
  `state`: The state.
  `path`: A path inside the state with a coercion result for the input.
  `set-fn`: 0-arg callback called to set the value."
  [{:keys [state path set-fn]}]
  (if-let [err-msg (-> @state (get-in path) kws.coercion.result/error-message)]
    [error-message-box/main {:value err-msg}]
    [:div.cards-grid-config-dashboard__set-wrapper
     [:button.cards-grid-config-dashboard__set {:on-click #(set-fn)}
      "Set"]]))

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
   [:button.clear-button {:on-click #(goto-cards-grid!)}
    [icons/arrow-left]]])

;; Coercers
(def positive-int-or-nil-coercer
  (coercion/->Or [#(-> % coercers/empty)
                  #(-> % coercers/integer coercers/positive)]))

;; Inputs
(defn- page-config
  "A config input for a page"
  [{:keys [state] ::kws/keys [set-page!]}]
  [:div.cards-grid-config-dashboard__row
   (label "Page")
   [input-wrapper {}
    [form.input/main (input-props state [kws/page] positive-int-or-nil-coercer)]]
   [set-btn {:state state
             :path [kws/page]
             :set-fn #(-> @state kws/page kws.coercion.result/value set-page!)}]])

(defn- page-size-config
  "A config input for page size"
  [{:keys [state] ::kws/keys [set-page-size!]}]
  [:div.cards-grid-config-dashboard__row
   (label "Page Size")
   [input-wrapper {}
    [form.input/main (input-props state [kws/page-size] positive-int-or-nil-coercer)]]
   [set-btn {:state state
             :path [kws/page-size]
             :set-fn #(-> @state kws/page-size kws.coercion.result/value set-page-size!)}]])

(defn- include-tags-config
  "A config for tags to include"
  [{:keys [state] ::kws/keys [set-include-tags!]}]
  [:div.cards-grid-config-dashboard__row
   (label "ALL tags")
   [input-wrapper {}
    [inputs.tags/main (input-props state [kws/include-tags] coercers/tags)]]
   [set-btn
    {:state state
     :path [kws/include-tags]
     :set-fn #(let [tags (->> @state kws/include-tags kws.coercion.result/value)]
                (swap! state assoc kws/include-tags (coercion.result/raw-value->success tags))
                (set-include-tags! tags))}]])

(defn- exclude-tags-config
  "A config for tags to exclude"
  [{:keys [state] ::kws/keys [set-exclude-tags!]}]
  [:div.cards-grid-config-dashboard__row
   (label "Not ANY tags")
   [input-wrapper {}
    [inputs.tags/main (input-props state [kws/exclude-tags] coercers/tags)]]
   [set-btn
    {:state state
     :path [kws/exclude-tags]
     :set-fn #(let [tags (->> @state kws/exclude-tags kws.coercion.result/value)]
                (swap! state assoc kws/exclude-tags (coercion.result/raw-value->success tags))
                (set-exclude-tags! tags))}]])

;; Main
(defn main
  "A configuration dashboard for the cards grid."
  [props]
  [:div.cards-grid-config-dashboard
   [header props]
   [page-config props]
   [page-size-config props]
   [include-tags-config props]
   [exclude-tags-config props]])
