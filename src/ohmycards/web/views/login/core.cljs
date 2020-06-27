(ns ohmycards.web.views.login.core
  (:require [cljs.core.async :as a]
            [ohmycards.web.components.error-message-box.core :as error-message-box]
            [ohmycards.web.components.form.core :as form]
            [ohmycards.web.components.form.input :as form.input]
            [ohmycards.web.components.loading-wrapper.core :as loading-wrapper]
            [ohmycards.web.views.login.email-row :as email-row]
            [ohmycards.web.views.login.handlers.submit :as handlers.submit]))

;; Components
(defn- submit-btn
  "A form row with the submit button."
  [_]
  [form/row {}
   [form/submit {}]])

(defn- one-time-password-row
  "A form row for inputting the one time password."
  [{:keys [onetime-password-sent? value on-change]}]
  (when onetime-password-sent?
    [form/row {}
     [form/label {}
      "Password"]
     [form.input/main {:type "password" :value value :on-change on-change :autoFocus true}]]))

(defn main
  "The login page, that allows the user to identify itself."
  [{:keys [state] :as props}]
  (let [{:keys [onetime-password-sent? email onetime-password error-message loading?]} @state]
    [:div.login-view {}
     [:h3 "Login"]
     [loading-wrapper/main {:loading? loading?}
      [form/main {::form/on-submit #(handlers.submit/main props)}
       [email-row/main (email-row/props-builder props)]
       [one-time-password-row {:onetime-password-sent? onetime-password-sent?
                               :value onetime-password
                               :on-change #(swap! state assoc :onetime-password %)}]
       [submit-btn]]
      [error-message-box/main {:value error-message}]]]))
