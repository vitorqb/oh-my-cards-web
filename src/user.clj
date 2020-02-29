(ns user (:require [ring.middleware.resource :refer [wrap-resource]]
                   [ring.util.response :refer [file-response]]
                   clojure.pprint))

;; Serves static resource from `public` then fallback to `index.html`.
(def app (comp (wrap-resource identity "public")
               (fn [_] (file-response "public/index.html"))))
