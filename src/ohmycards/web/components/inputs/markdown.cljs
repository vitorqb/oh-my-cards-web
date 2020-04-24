(ns ohmycards.web.components.inputs.markdown
  (:require ["react-mde" :default ReactMde]
            ["react-markdown" :as ReactMarkdown]
            [react :as react]
            [reagent.core :as r]
            [reagent.impl.template :as rtpl]))

(defn- render-markdown
  "Uses ReactMarkdown to render the markdown, returning a react component wrapped by a promise."
  [source]
  (js/Promise.
   (fn [resolve _]
     (resolve
      (r/as-element
       [:> ReactMarkdown {:source source}])))))

(def textarea-component
  "This is a hack needed so that the `textarea` component used by the `ReactMde` works.
  Refer to https://github.com/reagent-project/reagent/issues/265#issuecomment-397895508
  Notice that the `forwardRef` trick allows us to put a custom `textarea` component, but
  the `ref` the `ReactMde` will see will be the ref to the `textarea` component!"
  (react/forwardRef
   (fn textarea [props ref]
     (let [props (assoc (js->clj props) :ref ref)]
       (r/as-element [:textarea props])))))

(defn- react-mde
  "Simple wrapper around ReactMde using our custom textarea-component"
  [props]
  (let [props (rtpl/convert-prop-value (assoc props :text-area-component textarea-component))]
    (r/create-element ReactMde props)))

(defn main
  "An input for a markdown with preview."
  [{:keys [value on-change]}]
  (let [!tab (r/atom "write")]
    (fn [{:keys [value on-change]}]
      [react-mde {:on-change on-change
                  :value (or value "")
                  :selected-tab @!tab
                  :on-tab-change #(reset! !tab %)
                  :generate-markdown-preview #(render-markdown %)
                  :min-editor-height 300}])))
