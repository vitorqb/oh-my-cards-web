(ns ohmycards.web.views.login.email-row
  (:require [ohmycards.web.components.form.core :as form]))

(defn main
  "A form row for inputting the email."
  [{:keys [value on-change disabled?]}]
  [form/row {}
   [form/label {}
    "Email"]
   [form/input {:type "email"
                :value value
                :on-change on-change
                :disabled disabled?}]])

(defn props-builder
  [{:keys [state]}]
  {:disabled? (:onetime-password-sent? @state)
   :value (:email @state)
   :on-change #(swap! state assoc :email %)})
