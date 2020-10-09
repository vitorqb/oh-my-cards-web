(ns ohmycards.web.kws.common.cards.history.core)

;; History keywords
(def ^:const events        "Keyword containing the sequence of events." ::events)
(def ^:const datetime      "Keyword containing the datetime of a field update" ::datetime)
(def ^:const event-type    "Keyword containing the type of event of a field update" ::event-type)
(def ^:const field-updates "Keyword containing the sequence of field updates" ::field-updates)
(def ^:const field-name    "Keyword containing the name of a field that has been updated" ::field-name)
(def ^:const field-type    "Keyword containing the type of a field that has been updated" ::field-type)
(def ^:const old-value     "Keyword containing the old value of a field that has been updated" ::old-value)
(def ^:const new-value     "Keyword containing the new value of a field that has been updated" ::new-value)

;; Event types
(def ^:const event-creation "Keywords for an event of type `creation`" ::event-creation)
(def ^:const event-update   "Keywords for an event of type `update`"   ::event-update)
(def ^:const event-deletion "Keywords for an event of type `deletion`" ::event-deletion)

;; Field types
(def ^:const field-string "Keyword for a field of type string" ::field-string)
(def ^:const field-tags   "Keyword for a field of type tags"   ::field-tags)
