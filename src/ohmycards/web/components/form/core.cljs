(ns ohmycards.web.components.form.core
  (:require [reagent.core :as r]
            [ohmycards.web.utils.components :as utils.components]))

;; Handlers and helpers
(defn gen-submit-handler
  "Returns a submit handler that propagates to `on-submit`."
  [on-submit]
  (fn [event]
    (.preventDefault event)
    (on-submit)))

(defn gen-input-on-change-handler
  "Returns a on-change handler that calls `on-change` with the new input value."
  [on-change]
  (fn [event]
    (on-change (.-value (.-target event)))))

;; Components
(defn main
  "A wrapper for a form component.
  - `::on-submit`: A 0-arg fn called on form submission."
  [{::keys [on-submit]} & children]
  [:form.simple-form {:on-submit (gen-submit-handler on-submit)}
   (utils.components/with-seq-keys children)])

(defn row
  "A single row for the form, usually containing a label and an input."
  [_ & children]
  [:div.simple-form__input-row {}
   (utils.components/with-seq-keys children)])

(defn label
  "A label for a form input, usually comming before the input."
  [_ & children]
  [:div.simple-form__label {} children])

(defn input
  "An input for a form, usually comming alone in a form row."
  [props]
  [:input
   (-> props
       (update :on-change gen-input-on-change-handler)
       (update :class #(or % "simple-form__input")))])

(defn submit
  "A submit button form a form, usually in an empty row."
  [props]
  [:input (merge {:type "submit"} props)])
