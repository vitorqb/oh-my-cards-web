(ns ohmycards.web.kws.common.cards.history.core)

;; History keywords
(def events        "Keyword containing the sequence of events." ::events)
(def datetime      "Keyword containing the datetime of a field update" ::datetime)
(def event-type    "Keyword containing the type of event of a field update" ::event-type)
(def field-updates "Keyword containing the sequence of field updates" ::field-updates)
(def field-name    "Keyword containing the name of a field that has been updated" ::field-name)
(def field-type    "Keyword containing the type of a field that has been updated" ::field-type)
(def old-value     "Keyword containing the old value of a field that has been updated" ::old-value)
(def new-value     "Keyword containing the new value of a field that has been updated" ::new-value)

;; Event types
(def event-creation "Keywords for an event of type `creation`" ::event-creation)
(def event-update   "Keywords for an event of type `update`"   ::event-update)
(def event-deletion "Keywords for an event of type `deletion`" ::event-deletion)

;; Field types
(def field-string "Keyword for a field of type string" ::field-string)
(def field-tags   "Keyword for a field of type tags"   ::field-tags)
