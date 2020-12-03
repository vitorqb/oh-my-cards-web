(ns ohmycards.web.kws.components.dialog.core)

;; State
(def active? "Is the dialog active?" ::active?)

;; Props
(def on-show! "Callback called when the dialog is shown" ::on-show!)
(def on-hide! "Callback called when the dialog is hidden" ::on-hide!)
