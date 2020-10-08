(ns ohmycards.web.services.fetch-be-version.core
  (:require [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.protocols.http :as protocols.http]))

(defrecord Action []
  protocols.http/HttpAction
  (protocols.http/url [_] "/version")
  (protocols.http/method [_] :GET)
  (protocols.http/parse-success-response [_ r] (kws.http/body r))
  (protocols.http/parse-error-response [_ _] "???"))
