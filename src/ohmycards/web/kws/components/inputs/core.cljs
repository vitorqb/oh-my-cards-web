(ns ohmycards.web.kws.components.inputs.core
  (:refer-clojure :exclude [type]))

;; Generic props
(def itype "The type of input to render" ::itype)
(def props "Extra props for the underlying input implementation" ::props)
(def cursor "An atom-like that keeps the value of the input" ::cursor)
(def coercer "A coercer to use when setting the value from the raw input value" ::coercer)
(def disabled? "If true, set's `:disabled` to the input" ::disabled?)
(def auto-focus "If true, set's `:auto-focus` to the input" ::auto-focus)

;; Input types
(def t-simple ::t-simple)
(def t-tags ::t-tags)
(def t-combobox ::t-combobox)
(def t-markdown ::t-markdown)
(def t-textarea ::t-textarea)
