(ns ohmycards.web.services.login.utils
  (:require [ohmycards.web.kws.user :as kws.user]))

(defn user-logged-in?
  "Returns ture if the user should be considered logged in, false otherwise."
  [{::kws.user/keys [token email]}]
  ;; Right now, having a token+email means we have a token that we know it works,
  ;; because the email comes from BE.
  (boolean (and token email)))
