(ns ohmycards.web.components.form.core
  (:require [ohmycards.web.components.form.input :as input]
            [ohmycards.web.utils.components :as utils.components]
            [reagent.core :as r]))

;; Handlers and helpers
(defn gen-submit-handler
  "Returns a submit handler that propagates to `on-submit`."
  [on-submit]
  (fn [event]
    (.preventDefault event)
    (on-submit)))

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

(defn submit
  "A submit button form a form, usually in an empty row."
  [props]
  [:input (merge {:type "submit"} props)])
