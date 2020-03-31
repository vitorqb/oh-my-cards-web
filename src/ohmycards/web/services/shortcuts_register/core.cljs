(ns ohmycards.web.services.shortcuts-register.core
  (:require goog.events
            [ohmycards.web.kws.services.shortcuts-register.core :as kws])
  (:import goog.ui.KeyboardShortcutHandler))

;; Constants
(def ^:private SHORTCUT-TRIGGERED goog.ui.KeyboardShortcutHandler.EventType.SHORTCUT_TRIGGERED)

;; Helpers
(defn- log [x] (js/console.log (str "[Services.ShortcutsRegister] " x)))

(defn- register-shortcut!
  [{::kws/keys [id key-desc callback]}]
  (log (str "Registering shortcut with id " id))
  (let [shortcut-handler (goog.ui.KeyboardShortcutHandler. js/document)]
    (.registerShortcut shortcut-handler (str id) key-desc)
    (goog.events/listen shortcut-handler SHORTCUT-TRIGGERED callback)))

;; API
(defn init!
  "Initializes the shortcut register and listen to all shortcuts given.
  `shortcuts` is a map conforming to `kws` specifications."
  [shortcuts]
  (log "Initializing...")
  (doseq [s shortcuts]
    (register-shortcut! s)))
