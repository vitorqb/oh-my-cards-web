(ns ohmycards.web.kws.services.login.core)

(def send-onetime-password-action
  "An action representing that requested the BE to send a onetime password."
  ::send-onetime-password-action)

(def get-token-action
  "An action representing that requested the BE to get a token."
  ::get-token-action)

(def action "The login action." ::action)

(def onetime-password "The onetime password the user is inputting." ::onetime-password)
(def error-message "An error message." ::error-message)
(def token "The returned token." ::token)
