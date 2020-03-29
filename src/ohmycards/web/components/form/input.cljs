(ns ohmycards.web.components.form.input)

(defn- gen-input-on-change-handler
  "Returns a on-change handler that calls `on-change` with the new input value."
  [on-change]
  (fn [event]
    (on-change (.-value (.-target event)))))

(defn build-props
  "A helper function to build input props based on a state and a path. It basically assocs
  `value` and `on-change`. All `kargs` key-value pairs assoc assoced to the built props.
  If `disabled` is set on kargs, then skip associng `on-change` handler.
  `parse-fn` is a function that parses the value before it is stored in the state."
  [state path & {:keys [disabled parse-fn] :or {parse-fn identity} :as kargs}]
  (cond-> {}
    :always        (assoc :value (get-in @state path))
    (not disabled) (assoc :on-change #(swap! state assoc-in path (parse-fn %)))
    :always        (merge kargs)))

(defn main
  "An input for a form, usually comming alone in a form row."
  [props]
  [:input
   (-> props
       (update :on-change gen-input-on-change-handler)
       (update :class #(or % "simple-form__input")))])
