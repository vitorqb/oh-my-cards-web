(ns ohmycards.web.views.login.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.core :as inputs]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.kws.components.inputs.core :as kws.inputs]
            [ohmycards.web.views.login.email-row :as email-row]
            [ohmycards.web.views.login.handlers.submit :as handlers.submit]
            [reagent.core :as r]))

;; Components
(defn- submit-btn
  "A form row with the submit button."
  [_]
  [form/row {:input [form/submit]}])

(defn- one-time-password-row
  "A form row for inputting the one time password."
  [{:keys [onetime-password-sent? cursor]}]
  (when onetime-password-sent?
    [form/row
     {:label "Password"
      :input [inputs/main
              {kws.inputs/props {:type "password" :auto-focus true}
               kws.inputs/cursor cursor}]}]))

(defn main
  "The login page, that allows the user to identify itself."
  [{:keys [state] :as props}]
  (let [{:keys [onetime-password-sent? email onetime-password error-message loading?]} @state]
    [:div.login-view {}
     [:div.login-view__header
      [:h3 "Login"]]
     [:div.login-view__body
      [loading-wrapper/main {:loading? loading?}
       [form/main {::form/on-submit #(handlers.submit/main props)}
        [email-row/main props]
        [one-time-password-row {:onetime-password-sent? onetime-password-sent?
                                :cursor (r/cursor state [:onetime-password])}]
        [:div.u-center
         [submit-btn]]]
       [error-message-box/main {:value error-message}]]]]))
