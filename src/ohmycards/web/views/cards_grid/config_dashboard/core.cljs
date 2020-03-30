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

(defn- set-btn [f]
  [:div.cards-grid-config-dashboard__set-wrapper
   [:button.cards-grid-config-dashboard__set {:on-click #(f)} "Set"]])

(defn- input-wrapper
  [_ & children]
  (into [:div.cards-grid-config-dashboard__input-wrapper] children))

(defn input-props
  [state path & args]
  (apply form.input/build-props state path :class "cards-grid-config-dashboard__input" args))

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
    [form.input/main (input-props state [kws/page]
                                  :parse-fn #(coercion/main % positive-int-or-nil-coercer)
                                  :unparse-fn kws.coercion.result/raw-value)]]
   (if-let [err-msg (-> @state kws/page kws.coercion.result/error-message)]
     [error-message-box/main {:value (-> @state kws/page kws.coercion.result/error-message)}]
     (set-btn #(set-page! (-> @state kws/page kws.coercion.result/value))))])

(defn- page-size-config
  "A config input for page size"
  [{:keys [state] ::kws/keys [set-page-size!]}]
  [:div.cards-grid-config-dashboard__row
   (label "Page Size")
   [input-wrapper {}
    [form.input/main (input-props state [kws/page-size]
                                  :parse-fn #(coercion/main % positive-int-or-nil-coercer)
                                  :unparse-fn kws.coercion.result/raw-value)]]
   (if-let [err-msg (-> @state kws/page-size kws.coercion.result/error-message) ]
     [error-message-box/main {:value err-msg}]
     (set-btn #(set-page-size! (-> @state kws/page-size kws.coercion.result/value))))])

(defn- include-tags-config
  "A config for tags to include"
  [{:keys [state] ::kws/keys [set-include-tags!]}]
  [:div.cards-grid-config-dashboard__row
   (label "ALL tags")
   [input-wrapper {}
    [inputs.tags/main (input-props state [kws/include-tags]
                                   :parse-fn #(coercion/main % coercers/tags)
                                   :unparse-fn kws.coercion.result/raw-value)]]
   (if-let [err-msg (-> @state kws/include-tags kws.coercion.result/error-message)]
     [error-message-box/main {:value err-msg}]
     (set-btn #(let [tags (->> @state kws/include-tags kws.coercion.result/value)]
                 (swap! state assoc kws/include-tags (coercion.result/raw-value->success tags))
                 (set-include-tags! tags))))])

(defn- exclude-tags-config
  "A config for tags to exclude"
  [{:keys [state] ::kws/keys [set-exclude-tags!]}]
  [:div.cards-grid-config-dashboard__row
   (label "Not ANY tags")
   [input-wrapper {}
    [inputs.tags/main (input-props state [kws/exclude-tags])]]
   (set-btn #(let [tags (->> @state kws/exclude-tags tags/sanitize vec)]
               (swap! state assoc kws/exclude-tags tags)
               (set-exclude-tags! tags)))])

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
