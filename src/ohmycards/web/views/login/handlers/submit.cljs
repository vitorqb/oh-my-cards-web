(ns ohmycards.web.views.login.handlers.submit
  (:require [ohmycards.web.kws.http :as kws.http]
            [cljs.core.async :as a]
            [ohmycards.web.kws.services.login.core :as services.login.kws]
            [ohmycards.web.kws.user :as kws.user]))

(defn- before-submit
  "Reducer applied to state before the login submit action."
  [state]
  (assoc state
         :loading? true
         :error-message nil
         :token nil))

(defn- after-submit
  "Reducer applied to state after the login submit call returns."
  [state {::services.login.kws/keys [error-message token action] :as response}]
  (cond-> state
    :always
    (assoc :loading? false)

    error-message
    (assoc :error-message error-message)
    
    token
    (assoc :token token)
    
    (and (= action services.login.kws/send-onetime-password-action)
         (not error-message))
    (assoc :onetime-password-sent? true
           :onetime-password "")))

(defn main
  "Handles state during login submission."
  [{:keys [state save-user-fn login-fn]}]
  (let [{:keys [email onetime-password loading?]} @state]
    (when-not loading?
      (a/go
        (swap! state before-submit)
        (let [login-result (-> {::services.login.kws/onetime-password onetime-password
                                ::services.login.kws/email email}
                               (login-fn)
                               a/<!)]
          (swap! state after-submit login-result)
          (when-let [token (:token @state)]
            (save-user-fn {::kws.user/email email ::kws.user/token token})))))))
