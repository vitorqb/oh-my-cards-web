(ns ohmycards.web.components.inputs.tags
  (:require [ohmycards.web.components.form.core :as form]))

;; Helpers
(defn- zip-tags
  "Returns a sequence of [tag-value key index] for a given sequence of tags.
  `tag-value` is the value of the tag, `key` is the key to identify that tag and
  `index` is the index where the tag value must be appended on the tags vector."
  [tags]
  (for [i (-> tags count inc range)
        :let [tag-value (get tags i)
              key       i
              index     (if (= i (count tags)) :append i)]]
    [tag-value key index]))

;; Handlers
(defn- tag-change-handler
  "Returns a on-change handler function for a tag.
  `props`: The props of main.
  `i`: The index of the tag that must be changed."
  [{:keys [value on-change]} i]
  (cond
    (= i :append) #(on-change (vec (conj value %)))
    (number? i)   #(on-change (vec (assoc value i %)))))

;; Components
(defn- single-tag-input
  "An input for a single tag"
  [{:keys [value on-change]}]
  [form/input {:class "tags-input__input"
               :type "text"
               :value value
               :on-change on-change}])

(defn main
  "An input for tags."
  [{:keys [value on-change] :as props}]
  [:div.tags-input
   (for [[tag key index] (zip-tags value)]
     [single-tag-input {:key key :value tag :on-change (tag-change-handler props index)}])])
