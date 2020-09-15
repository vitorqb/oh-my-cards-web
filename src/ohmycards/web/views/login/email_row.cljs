(ns ohmycards.web.views.login.email-row
  (:require [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [reagent.core :as r]))

(defn main
  "A form row for inputting the email."
  [{:keys [state]}]
  [form/row
   {:label "Email"
    :input [inputs/main
            {kws.inputs/props {:type "email"}
             kws.inputs/cursor (r/cursor state [:email])
             kws.inputs/disabled? (:onetime-password-sent? @state)}]}])
