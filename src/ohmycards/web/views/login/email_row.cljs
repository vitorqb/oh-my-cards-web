(ns ohmycards.web.views.login.email-row
  (:require [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.form.input :as form.input]))

(defn main
  "A form row for inputting the email."
  [{:keys [value on-change disabled?]}]
  [form/row {:label "Email"
             :input [form.input/main {:type       "email"
                                      :value      value
                                      :on-change  on-change
                                      :disabled   disabled?}]}])

(defn props-builder
  [{:keys [state]}]
  {:disabled? (:onetime-password-sent? @state)
   :value (:email @state)
   :on-change #(swap! state assoc :email %)})
