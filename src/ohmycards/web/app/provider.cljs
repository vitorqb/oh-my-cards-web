(ns ohmycards.web.app.provider
  "This namespace puts together different services and dependencies and provides them
  to the `core.cljs` and the contorllers."

  (:require [ohmycards.web.services.notify :as services.notify]
            [ohmycards.web.utils.clipboard :as utils.clipboard]))

(defn to-clipboard!
  "Sends a text to the clipboard and notifies user on completion."
  [txt]
  (utils.clipboard/to-clipboard! txt)
  (services.notify/notify! "Copied to clipboard!"))
