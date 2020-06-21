(ns ohmycards.web.services.fetch-be-version.core
  (:require [cljs.core.async :as async]
            [ohmycards.web.kws.http :as kws.http]))

(defn main
  "Fetches the BE version and returns it as a string."
  [{:keys [http-fn]}]
  (async/go
    (-> (http-fn kws.http/url "/version" kws.http/method :GET)
        async/<!
        kws.http/body)))
