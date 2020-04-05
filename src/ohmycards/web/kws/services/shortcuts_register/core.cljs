(ns ohmycards.web.kws.services.shortcuts-register.core)

(def id "Keyword or string to identify the shortcut" ::id)
(def key-desc
  "A string describing the shortcut: see examples in 
   https://google.github.io/closure-library/api/goog.ui.KeyboardShortcutHandler.html"
  ::key-desc)
(def callback "0-arg callback function to call." ::callback)
