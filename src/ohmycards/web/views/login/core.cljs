(ns ohmycards.web.views.login.core
  (:require [ohmycards.web.components.form.core :as form]
            [cljs.core.async :as a]))

;; Handlers
(defn- handle-form-submit []
  nil)

;; Components
(defn- email-row
  "A form row for inputting the email."
  [_]
  [form/row {}
   [form/label {}
    "Email"]
   [form/input {:type "email"}]])

(defn- submit-btn
  "A form row with the submit button."
  [_]
  [form/row {}
   [form/submit {}]])

(defn main
  "The login page, that allows the user to identify itself."
  []
  [:div.login-view {}
   [:h3 "Login"]
   [form/main {}
    [email-row {}]
    [submit-btn {}]]])
