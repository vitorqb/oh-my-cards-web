(ns dev.core
  (:require [cljs-http.client :as http]
            [cljs.repl :refer [doc]]
            [cljs.test :refer-macros [run-tests]]
            [ohmycards.web.app.state :as app.state]
            [ohmycards.web.core :as core]
            [ohmycards.web.kws.lenses.login :as lenses.login]
            [ohmycards.web.kws.user :as kws.user]
            test-ns-requires))

;; App shortcuts
(def state app.state/state)

(defn get-token [] (-> @state lenses.login/current-user kws.user/token))

(defn synchronize-ES
  "Shortcut to synchronize ES and the DB."
  []
  (http/request {:method :POST
                 :headers {"Authorization" (str "Bearer " (get-token))}
                 :url "/admin/synchronize-ES"
                 :with-credentials? false}))
