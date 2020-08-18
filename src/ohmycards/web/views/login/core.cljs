(ns ohmycards.web.views.login.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.inputs.simple :as inputs.simple]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.views.login.email-row :as email-row]
            [ohmycards.web.views.login.handlers.submit :as handlers.submit]))

;; Components
(defn- submit-btn
  "A form row with the submit button."
  [_]
  [form/row {:input [form/submit]}])

(defn- one-time-password-row
  "A form row for inputting the one time password."
  [{:keys [onetime-password-sent? value on-change]}]
  (when onetime-password-sent?
    (let [input-props {:type      "password"
                       :value     value
                       :on-change on-change
                       :autoFocus true}
          input       [inputs.simple/main input-props]]
      [form/row {:label "Password"
                 :input input}])))

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
        [email-row/main (email-row/props-builder props)]
        [one-time-password-row {:onetime-password-sent? onetime-password-sent?
                                :value onetime-password
                                :on-change #(swap! state assoc :onetime-password %)}]
        [:div.u-center
         [submit-btn]]]
       [error-message-box/main {:value error-message}]]]]))
