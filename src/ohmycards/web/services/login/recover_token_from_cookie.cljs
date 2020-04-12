(ns ohmycards.web.services.login.recover-token-from-cookie
  (:require [cljs.core.async :as a]
            [ohmycards.web.common.utils :as utils]
            [ohmycards.web.kws.http :as kws.http]))

;; Private fns
(defn- extract-token-from-token-recovery-response
  [{token ::kws.http/body success? ::kws.http/success?}]
  (and success? token))

(defn- token-recovery-http-call!
  "Runs the call to retrieve the token from cookies."
  [{:keys [state http-fn]}]
  (http-fn
   ::kws.http/url "/v1/auth/tokenRecovery"
   ::kws.http/method :post))

(defn- main!
  "Tries to get a token using browser cookies. Returns either `:notoken` or the token."
  [opts]
  (let [http-resp (token-recovery-http-call! opts)]
    (utils/with-out-chan [out]
      (a/go
        (if-let [resp (extract-token-from-token-recovery-response (a/<! http-resp))]
          (a/>! out resp)
          (a/close! out))))))

