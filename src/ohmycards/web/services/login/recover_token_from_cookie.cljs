(ns ohmycards.web.services.login.recover-token-from-cookie
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.utils :as utils]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.protocols.http :as protocols.http]))

(defrecord Action []
  protocols.http/HttpAction
  (protocols.http/url [_] "/v1/auth/tokenRecovery")
  (protocols.http/method [_] :POST)
  (protocols.http/parse-success-response [_ r] (kws.http/body r))
  (protocols.http/parse-error-response [_ _] nil))

(defn- main!
  "Tries to get and return a token using browser cookies."
  [{:keys [run-http-action-fn]}]
  (run-http-action-fn (->Action)))

