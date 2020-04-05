(ns ohmycards.web.components.inputs.combobox
  (:require ["@reach/combobox" :as combobox]
            [ohmycards.web.common.string.fuzzy-match :as fuzzy-match]
            [ohmycards.web.kws.components.inputs.combobox.core :as kws]
            [ohmycards.web.kws.components.inputs.combobox.options :as kws.options]
            [reagent.core :as r]))

;; Wrappers
(def ^:private Combobox (r/adapt-react-class combobox/Combobox))
(def ^:private ComboboxInput (r/adapt-react-class combobox/ComboboxInput))
(def ^:private ComboboxPopover (r/adapt-react-class combobox/ComboboxPopover))
(def ^:private ComboboxList (r/adapt-react-class combobox/ComboboxList))
(def ^:private ComboboxOption (r/adapt-react-class combobox/ComboboxOption))

;; Helpers
(defn- filter-options-by-value
  "Selects the options to display based on user input"
  [options value]
  (let [values-set (->> options (map kws.options/value) (fuzzy-match/main (or value "")) set)]
    (filter #(values-set (kws.options/value %)) options)))

(defn main
  "A combobox (multiselect)."
  [{:keys [value on-change class] ::kws/keys [options] :as props}]
  [Combobox {:on-select #(on-change %)}
   [ComboboxInput {:value value :on-change #(-> % .-target .-value on-change) :autocomplete true}]
   [ComboboxPopover {}
    (into
     [ComboboxList {}]
     (for [o (filter-options-by-value options value)]
       [ComboboxOption {:value (kws.options/value o)}]))]])
