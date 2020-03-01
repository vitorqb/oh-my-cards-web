(ns ohmycards.web.views.login.handlers.submit
  (:require [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.services.login.core :as services.login]
            [cljs.core.async :as a]
            [ohmycards.web.kws.services.login.core :as services.login.kws]))

(defn- before-submit
  "Reducer applied to state before the login submit action."
  [state]
  (assoc state
         :loading? true
         :error-message nil))

(defn- after-submit
  "Reducer applied to state after the login submit call returns."
  [state {::services.login.kws/keys [error-message token action]}]
  (cond-> state
    :always
    (assoc :loading? false)

    error-message
    (assoc :error-message error-message)
    
    token
    (assoc :token token)

    (= action services.login.kws/send-onetime-password-action)
    (assoc :onetime-password-sent? true
           :onetime-password "")))

(defn main
  "Handles state during login submission."
  [{:keys [state http-fn]}]
  (let [{:keys [email onetime-password loading?]} @state]
    (when-not loading?
      (a/go
        (swap! state before-submit)
        (let [login-result (-> {::services.login.kws/onetime-password onetime-password
                                ::services.login.kws/email email}
                               (services.login/main {:http-fn http-fn})
                               a/<!)]
          (swap! state after-submit login-result))))))
